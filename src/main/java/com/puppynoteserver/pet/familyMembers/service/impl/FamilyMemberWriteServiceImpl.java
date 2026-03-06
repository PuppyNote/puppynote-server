package com.puppynoteserver.pet.familyMembers.service.impl;

import com.puppynoteserver.alertHistory.entity.AlertDestinationType;
import com.puppynoteserver.firebase.FirebaseService;
import com.puppynoteserver.firebase.request.SendFirebaseDataDto;
import com.puppynoteserver.firebase.request.SendFirebaseServiceRequest;
import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.global.exception.PuppyNoteException;
import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.entity.enums.RoleType;
import com.puppynoteserver.pet.familyMembers.repository.FamilyMemberRepository;
import com.puppynoteserver.pet.familyMembers.service.FamilyMemberWriteService;
import com.puppynoteserver.pet.familyMembers.service.request.FamilyMemberInviteServiceRequest;
import com.puppynoteserver.pet.familyMembers.service.request.FamilyMemberRegisterServiceRequest;
import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.user.push.service.PushReadService;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.service.UserReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FamilyMemberWriteServiceImpl implements FamilyMemberWriteService {

    private final FamilyMemberRepository familyMemberRepository;
    private final UserReadService userReadService;
    private final PushReadService pushReadService;
    private final FirebaseService firebaseService;
    private final SecurityService securityService;

    @Override
    public void invite(FamilyMemberInviteServiceRequest request) {
        Long inviterUserId = securityService.getCurrentLoginUserInfo().getUserId();

        // 초대자의 강아지 목록 조회
        List<FamilyMember> ownerPets = familyMemberRepository.findAllOwnerPetsOf(inviterUserId);
        if (ownerPets.isEmpty()) {
            throw new PuppyNoteException("등록된 반려견이 없습니다.");
        }

        List<Long> petIds = ownerPets.stream()
                .map(fm -> fm.getId().getPetId())
                .toList();

        // 중복 초대 체크
        if (familyMemberRepository.existsByUserIdAndPetIds(request.getInviteeUserId(), petIds)) {
            throw new PuppyNoteException("이미 가족으로 등록되어 있거나 초대 대기 중인 유저입니다.");
        }

        User invitee = userReadService.findById(request.getInviteeUserId());
        User inviter = userReadService.findById(inviterUserId);

        // 초대자의 모든 강아지에 PENDING 레코드 생성
        for (FamilyMember ownerPet : ownerPets) {
            Pet pet = ownerPet.getPet();
            familyMemberRepository.save(FamilyMember.of(invitee, pet, RoleType.FAMILY, FamilyMemberStatus.PENDING));
        }

        // 초대 대상에게 Push 발송
        pushReadService.findByUserId(invitee.getId()).ifPresent(push ->
                firebaseService.sendPushNotification(
                        SendFirebaseServiceRequest.builder()
                                .push(push)
                                .sound("default")
                                .body(inviter.getNickName() + "님이 가족으로 초대했습니다!")
                                .sendFirebaseDataDto(SendFirebaseDataDto.builder()
                                        .alert_destination_type(AlertDestinationType.FAMILY_INVITE)
                                        .alert_destination_info(String.valueOf(inviterUserId))
                                        .build())
                                .build()
                )
        );
    }

    @Override
    public void register(FamilyMemberRegisterServiceRequest request) {
        Long currentUserId = securityService.getCurrentLoginUserInfo().getUserId();

        // 초대자의 강아지 목록 조회
        List<FamilyMember> ownerPets = familyMemberRepository.findAllOwnerPetsOf(request.getInviterUserId());
        List<Long> petIds = ownerPets.stream()
                .map(fm -> fm.getId().getPetId())
                .toList();

        // 현재 유저의 PENDING 레코드 조회 후 DONE으로 변경
        List<FamilyMember> pendingRecords = familyMemberRepository.findAllPendingByUserIdAndPetIds(currentUserId, petIds);
        if (pendingRecords.isEmpty()) {
            throw new PuppyNoteException("이미 가족으로 등록되어 있거나 초대받은 내역이 없습니다.");
        }

        pendingRecords.forEach(fm -> fm.updateStatus(FamilyMemberStatus.DONE));
    }

    @Override
    public void deleteAllByPetId(Long petId) {
        familyMemberRepository.deleteAllByPetId(petId);
    }
}
