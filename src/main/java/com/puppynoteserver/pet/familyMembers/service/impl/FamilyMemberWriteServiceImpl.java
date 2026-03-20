package com.puppynoteserver.pet.familyMembers.service.impl;

import com.puppynoteserver.alertHistory.entity.AlertDestinationType;
import com.puppynoteserver.expo.event.PushNotificationEvent;
import com.puppynoteserver.expo.request.SendPushDataDto;
import com.puppynoteserver.expo.request.SendPushServiceRequest;
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
import com.puppynoteserver.pet.pets.service.PetReadService;
import com.puppynoteserver.user.push.entity.Push;
import com.puppynoteserver.user.push.service.PushReadService;
import org.springframework.context.ApplicationEventPublisher;
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
    private final PetReadService petReadService;
    private final PushReadService pushReadService;
    private final ApplicationEventPublisher eventPublisher;
    private final SecurityService securityService;

    @Override
    public void invite(FamilyMemberInviteServiceRequest request) {
        Long inviterUserId = securityService.getCurrentLoginUserInfo().getUserId();

        FamilyMember ownerRecord = familyMemberRepository.findByUserIdAndPetId(inviterUserId, request.getPetId())
                .orElseThrow(() -> new NotFoundException("해당 펫을 찾을 수 없습니다."));
        if (ownerRecord.getRole() != RoleType.OWNER) {
            throw new PuppyNoteException("펫의 OWNER만 가족을 초대할 수 있습니다.");
        }

        if (familyMemberRepository.existsByUserIdAndPetIds(request.getInviteeUserId(), List.of(request.getPetId()))) {
            throw new PuppyNoteException("이미 가족으로 등록되어 있거나 초대 대기 중인 유저입니다.");
        }

        User invitee = userReadService.findById(request.getInviteeUserId());
        User inviter = userReadService.findById(inviterUserId);

        familyMemberRepository.save(FamilyMember.of(invitee, petReadService.findById(request.getPetId()), RoleType.FAMILY, FamilyMemberStatus.PENDING));

        List<Push> pushes = pushReadService.findAllByUserId(invitee.getId());
        if (!pushes.isEmpty()) {
            eventPublisher.publishEvent(new PushNotificationEvent(
                    pushes,
                    SendPushServiceRequest.builder()
                            .push(pushes.get(0))
                            .sound("default")
                            .body(inviter.getNickName() + "님이 가족으로 초대했습니다!")
                            .sendPushDataDto(SendPushDataDto.builder()
                                    .alert_destination_type(AlertDestinationType.FAMILY_INVITE)
                                    .alert_destination_info("{\"userId\":" + request.getInviteeUserId() + ",\"petId\":" + request.getPetId() + "}")
                                    .build())
                            .build()
            ));
        }
    }

    @Override
    public void register(FamilyMemberRegisterServiceRequest request) {
        FamilyMember pendingRecord = familyMemberRepository.findByUserIdAndPetId(request.getUserId(), request.getPetId())
                .orElseThrow(() -> new NotFoundException("초대받은 내역이 없습니다."));
        if (pendingRecord.getStatus() != FamilyMemberStatus.PENDING) {
            throw new PuppyNoteException("이미 가족으로 등록되어 있습니다.");
        }

        pendingRecord.updateStatus(FamilyMemberStatus.DONE);
    }

    @Override
    public void deleteFamilyRelation(Long targetUserId, Long petId) {
        Long currentUserId = securityService.getCurrentLoginUserInfo().getUserId();

        FamilyMember currentUserRecord = familyMemberRepository.findByUserIdAndPetId(currentUserId, petId)
                .orElseThrow(() -> new NotFoundException("해당 펫의 가족 구성원 정보를 찾을 수 없습니다."));

        if (currentUserRecord.getRole() == RoleType.OWNER) {
            FamilyMember targetRecord = familyMemberRepository.findByUserIdAndPetId(targetUserId, petId)
                    .orElseThrow(() -> new NotFoundException("삭제할 가족 관계를 찾을 수 없습니다."));
            if (targetRecord.getRole() != RoleType.FAMILY) {
                throw new PuppyNoteException("FAMILY 멤버만 삭제할 수 있습니다.");
            }
            familyMemberRepository.deleteAllByUserIdAndPetIds(targetUserId, List.of(petId));
        } else {
            FamilyMember ownerRecord = familyMemberRepository.findByUserIdAndPetId(targetUserId, petId)
                    .orElseThrow(() -> new NotFoundException("삭제할 가족 관계를 찾을 수 없습니다."));
            if (ownerRecord.getRole() != RoleType.OWNER) {
                throw new PuppyNoteException("FAMILY는 FAMILY를 삭제할 수 없습니다.");
            }
            familyMemberRepository.deleteAllByUserIdAndPetIds(currentUserId, List.of(petId));
        }
    }

    @Override
    public void deleteAllByPetId(Long petId) {
        familyMemberRepository.deleteAllByPetId(petId);
    }
}
