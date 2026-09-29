package com.puppynoteserver.pet.familyMembers.application;

import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.global.exception.PuppyNoteException;
import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.pet.familyMembers.application.port.in.FamilyMemberFinder;
import com.puppynoteserver.pet.familyMembers.application.port.in.FamilyMemberRegister;
import com.puppynoteserver.pet.familyMembers.application.port.in.FamilyMemberRemover;
import com.puppynoteserver.pet.familyMembers.application.port.in.request.FamilyMemberInviteServiceRequest;
import com.puppynoteserver.pet.familyMembers.application.port.in.request.FamilyMemberRegisterServiceRequest;
import com.puppynoteserver.pet.familyMembers.application.port.in.response.FamilyMemberResponse;
import com.puppynoteserver.pet.familyMembers.application.port.in.response.UserSearchResponse;
import com.puppynoteserver.pet.familyMembers.application.port.out.UserProfileReader;
import com.puppynoteserver.pet.familyMembers.application.port.out.UserProfileReader.UserProfile;
import com.puppynoteserver.pet.familyMembers.application.port.out.persistence.FamilyMemberRepository;
import com.puppynoteserver.pet.familyMembers.domain.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.domain.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.domain.entity.enums.RoleType;
import com.puppynoteserver.pet.familyMembers.domain.error.FamilyMemberErrorMessage;
import com.puppynoteserver.pet.pets.domain.entity.Pet;
import com.puppynoteserver.storage.application.port.out.FileStorage;
import com.puppynoteserver.storage.enums.BucketKind;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FamilyMemberService implements FamilyMemberFinder, FamilyMemberRegister, FamilyMemberRemover {

    private final FamilyMemberRepository familyMemberRepository;
    private final UserProfileReader userProfileReader;
    private final SecurityService securityService;
    private final FileStorage fileStorage;

    @Override
    @Transactional(readOnly = true)
    public List<FamilyMemberResponse> getFamilyMembers(Long petId) {
        Long currentUserId = securityService.getCurrentLoginUserInfo().getUserId();

        List<FamilyMember> members = familyMemberRepository.findAllByPetIdAndStatus(petId, FamilyMemberStatus.DONE)
                .stream()
                .filter(fm -> !fm.getUserId().equals(currentUserId))
                .toList();

        Map<Long, UserProfile> profiles = userProfileReader.findAllByIds(members.stream().map(FamilyMember::getUserId).toList());

        return members.stream()
                .map(fm -> {
                    UserProfile profile = profiles.get(fm.getUserId());
                    String profileUrl = profile == null ? null : fileStorage.getCloudFrontUrl(profile.profileUrl(), BucketKind.USER_PROFILE);
                    return FamilyMemberResponse.of(fm, profile == null ? null : profile.nickName(), profileUrl);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findFamilyUserIds(Long userId) {
        return familyMemberRepository.findDirectFamilyUserIds(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FamilyMember> findByUserIdAndPetId(Long userId, Long petId) {
        return familyMemberRepository.findByUserIdAndPetId(userId, petId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSearchResponse> searchUsersByEmail(String email) {
        Long currentUserId = securityService.getCurrentLoginUserInfo().getUserId();

        return userProfileReader.searchByEmailLike(email, currentUserId).stream()
                .map(profile -> UserSearchResponse.of(profile, fileStorage.getCloudFrontUrl(profile.profileUrl(), BucketKind.USER_PROFILE)))
                .toList();
    }

    @Override
    public void invite(FamilyMemberInviteServiceRequest request) {
        Long inviterUserId = securityService.getCurrentLoginUserInfo().getUserId();

        FamilyMember ownerRecord = familyMemberRepository.findByUserIdAndPetId(inviterUserId, request.getPetId())
                .orElseThrow(() -> new NotFoundException(FamilyMemberErrorMessage.PET_NOT_FOUND.getMessage()));
        if (ownerRecord.getRole() != RoleType.OWNER) {
            throw new PuppyNoteException(FamilyMemberErrorMessage.INVITE_NOT_ALLOWED.getMessage());
        }

        if (familyMemberRepository.existsByUserIdAndPetIds(request.getInviteeUserId(), List.of(request.getPetId()))) {
            throw new PuppyNoteException(FamilyMemberErrorMessage.ALREADY_FAMILY.getMessage());
        }

        familyMemberRepository.save(FamilyMember.of(request.getInviteeUserId(), ownerRecord.getPet(), RoleType.FAMILY, FamilyMemberStatus.PENDING));

        // TODO: 초대 푸시 알림 - notification이 별도 서비스로 분리되면서 인프로세스 이벤트 발행이 불가능해졌다.
        // notification 쪽에 REST/이벤트 기반 알림 API가 생기면 다시 연결한다.
    }

    @Override
    public void register(FamilyMemberRegisterServiceRequest request) {
        FamilyMember pendingRecord = familyMemberRepository.findByUserIdAndPetId(request.getUserId(), request.getPetId())
                .orElseThrow(() -> new NotFoundException(FamilyMemberErrorMessage.INVITATION_NOT_FOUND.getMessage()));
        if (pendingRecord.getStatus() != FamilyMemberStatus.PENDING) {
            throw new PuppyNoteException(FamilyMemberErrorMessage.ALREADY_REGISTERED.getMessage());
        }

        pendingRecord.updateStatus(FamilyMemberStatus.DONE);
    }

    @Override
    public void registerOwner(Long userId, Pet pet) {
        familyMemberRepository.save(FamilyMember.of(userId, pet, RoleType.OWNER, FamilyMemberStatus.DONE));
    }

    @Override
    public void registerFamilyMembers(List<Long> userIds, Pet pet) {
        userIds.forEach(userId -> familyMemberRepository.save(FamilyMember.of(userId, pet, RoleType.FAMILY, FamilyMemberStatus.DONE)));
    }

    @Override
    public void deleteFamilyRelation(Long targetUserId, Long petId) {
        Long currentUserId = securityService.getCurrentLoginUserInfo().getUserId();

        FamilyMember currentUserRecord = familyMemberRepository.findByUserIdAndPetId(currentUserId, petId)
                .orElseThrow(() -> new NotFoundException(FamilyMemberErrorMessage.FAMILY_MEMBER_NOT_FOUND.getMessage()));

        if (currentUserRecord.getRole() == RoleType.OWNER) {
            FamilyMember targetRecord = familyMemberRepository.findByUserIdAndPetId(targetUserId, petId)
                    .orElseThrow(() -> new NotFoundException(FamilyMemberErrorMessage.RELATION_NOT_FOUND.getMessage()));
            if (targetRecord.getRole() != RoleType.FAMILY) {
                throw new PuppyNoteException(FamilyMemberErrorMessage.ONLY_FAMILY_CAN_BE_REMOVED.getMessage());
            }
            familyMemberRepository.deleteAllByUserIdAndPetIds(targetUserId, List.of(petId));

            return;
        }

        FamilyMember ownerRecord = familyMemberRepository.findByUserIdAndPetId(targetUserId, petId)
                .orElseThrow(() -> new NotFoundException(FamilyMemberErrorMessage.RELATION_NOT_FOUND.getMessage()));
        if (ownerRecord.getRole() != RoleType.OWNER) {
            throw new PuppyNoteException(FamilyMemberErrorMessage.FAMILY_CANNOT_REMOVE_FAMILY.getMessage());
        }
        familyMemberRepository.deleteAllByUserIdAndPetIds(currentUserId, List.of(petId));
    }

    @Override
    public void deleteAllByPetId(Long petId) {
        familyMemberRepository.deleteAllByPetId(petId);
    }
}
