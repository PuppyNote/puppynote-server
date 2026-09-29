package com.puppynoteserver.pet.familyMembers.application.port.in;

import com.puppynoteserver.pet.familyMembers.application.port.in.response.FamilyMemberResponse;
import com.puppynoteserver.pet.familyMembers.application.port.in.response.UserSearchResponse;
import com.puppynoteserver.pet.familyMembers.domain.entity.FamilyMember;

import java.util.List;
import java.util.Optional;

public interface FamilyMemberFinder {

    List<FamilyMemberResponse> getFamilyMembers(Long petId);

    List<UserSearchResponse> searchUsersByEmail(String email);

    /** 내가 직접 초대했거나 나를 직접 초대한 유저 id들. pets 서브도메인이 펫 생성 시 가족을 자동 추가할 때 쓴다. */
    List<Long> findFamilyUserIds(Long userId);

    Optional<FamilyMember> findByUserIdAndPetId(Long userId, Long petId);
}
