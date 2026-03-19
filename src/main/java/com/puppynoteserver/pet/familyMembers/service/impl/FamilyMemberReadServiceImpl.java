package com.puppynoteserver.pet.familyMembers.service.impl;

import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.pet.familyMembers.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.repository.FamilyMemberRepository;
import com.puppynoteserver.pet.familyMembers.service.FamilyMemberReadService;
import com.puppynoteserver.pet.familyMembers.service.response.FamilyMemberResponse;
import com.puppynoteserver.pet.familyMembers.service.response.UserSearchResponse;
import com.puppynoteserver.storage.enums.BucketKind;
import com.puppynoteserver.storage.service.S3StorageService;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.service.UserReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FamilyMemberReadServiceImpl implements FamilyMemberReadService {

    private final FamilyMemberRepository familyMemberRepository;
    private final UserReadService userReadService;
    private final SecurityService securityService;
    private final S3StorageService s3StorageService;

    @Override
    public List<FamilyMemberResponse> getFamilyMembers(Long petId) {
        Long currentUserId = securityService.getCurrentLoginUserInfo().getUserId();

        return familyMemberRepository.findAllByPetIdAndStatus(petId, FamilyMemberStatus.DONE)
                .stream()
                .filter(fm -> !fm.getId().getUserId().equals(currentUserId))
                .map(fm -> FamilyMemberResponse.of(fm, s3StorageService.getCloudFrontUrl(fm.getUser().getProfileUrl(), BucketKind.USER_PROFILE)))
                .toList();
    }

    @Override
    public List<User> findFamilyUsers(Long userId) {
        return familyMemberRepository.findDirectFamilyUsers(userId);
    }

    @Override
    public List<UserSearchResponse> searchUsersByEmail(String email) {
        Long currentUserId = securityService.getCurrentLoginUserInfo().getUserId();

        return userReadService.findAllByEmailLike(email)
                .stream()
                .filter(user -> !user.getId().equals(currentUserId))
                .map(user -> UserSearchResponse.of(user, s3StorageService.getCloudFrontUrl(user.getProfileUrl(), BucketKind.USER_PROFILE)))
                .toList();
    }
}
