package com.puppynoteserver.pet.familyMembers.repository;

import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;

import java.util.List;

public interface FamilyMemberRepository {

    FamilyMember save(FamilyMember familyMember);

    List<FamilyMember> findByPetIdWithUser(Long petId);
}
