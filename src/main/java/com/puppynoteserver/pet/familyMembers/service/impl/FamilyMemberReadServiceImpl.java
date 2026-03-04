package com.puppynoteserver.pet.familyMembers.service.impl;

import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.pet.familyMembers.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.repository.FamilyMemberRepository;
import com.puppynoteserver.pet.familyMembers.service.FamilyMemberReadService;
import com.puppynoteserver.pet.familyMembers.service.response.FamilyMemberResponse;
import com.puppynoteserver.pet.familyMembers.service.response.UserSearchResponse;
import com.puppynoteserver.user.users.service.UserReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FamilyMemberReadServiceImpl implements FamilyMemberReadService {

    private final FamilyMemberRepository familyMemberRepository;
    private final UserReadService userReadService;
    private final SecurityService securityService;

    @Override
    public List<FamilyMemberResponse> getFamilyMembers() {
        Long currentUserId = securityService.getCurrentLoginUserInfo().getUserId();

        // 내가 속한 모든 펫 ID 조회 (OWNER/FAMILY 역할 무관)
        List<Long> petIds = familyMemberRepository.findAllPetIdsByUserId(currentUserId);
        if (petIds.isEmpty()) {
            return List.of();
        }

        // 해당 펫들의 DONE 가족 목록 조회 후 유저 기준 중복 제거 (본인 제외)
        Map<Long, FamilyMemberResponse> uniqueMembers = new LinkedHashMap<>();
        familyMemberRepository.findAllByPetIdsAndStatus(petIds, FamilyMemberStatus.DONE)
                .stream()
                .filter(fm -> !fm.getId().getUserId().equals(currentUserId))
                .forEach(fm -> uniqueMembers.putIfAbsent(fm.getId().getUserId(), FamilyMemberResponse.of(fm)));

        return List.copyOf(uniqueMembers.values());
    }

    @Override
    public List<UserSearchResponse> searchUsersByEmail(String email) {
        return userReadService.findAllByEmailLike(email)
                .stream()
                .map(UserSearchResponse::of)
                .toList();
    }
}
