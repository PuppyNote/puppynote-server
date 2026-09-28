package com.puppynoteserver.pet.familyMembers.service;

import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;

import java.util.Optional;

public interface FamilyMemberService {

    FamilyMember save(FamilyMember familyMember);

    Optional<FamilyMember> findByUserIdAndPetId(Long userId, Long petId);
}
