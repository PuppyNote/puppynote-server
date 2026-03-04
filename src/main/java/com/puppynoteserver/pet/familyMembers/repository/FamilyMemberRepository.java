package com.puppynoteserver.pet.familyMembers.repository;

import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.entity.enums.RoleType;

import java.util.List;

public interface FamilyMemberRepository {

    FamilyMember save(FamilyMember familyMember);

    List<FamilyMember> findByPetIdWithUser(Long petId);

    List<FamilyMember> findAllByPetIdAndStatus(Long petId, FamilyMemberStatus status);

    List<FamilyMember> findAllByPetIdsAndStatus(List<Long> petIds, FamilyMemberStatus status);

    List<FamilyMember> findAllOwnerPetsOf(Long userId);

    List<FamilyMember> findAllPendingByUserIdAndPetIds(Long userId, List<Long> petIds);

    boolean existsByUserIdAndPetIds(Long userId, List<Long> petIds);
}
