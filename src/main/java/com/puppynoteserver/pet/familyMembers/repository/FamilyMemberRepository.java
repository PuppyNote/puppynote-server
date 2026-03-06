package com.puppynoteserver.pet.familyMembers.repository;

import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.entity.enums.RoleType;

import java.util.List;
import java.util.Optional;

public interface FamilyMemberRepository {

    FamilyMember save(FamilyMember familyMember);

    List<FamilyMember> findByPetIdWithUser(Long petId);

    List<FamilyMember> findAllByPetIdAndStatus(Long petId, FamilyMemberStatus status);

    List<FamilyMember> findAllByPetIdsAndStatus(List<Long> petIds, FamilyMemberStatus status);

    List<FamilyMember> findAllOwnerPetsOf(Long userId);

    List<Long> findAllPetIdsByUserId(Long userId);

    List<FamilyMember> findAllPendingByUserIdAndPetIds(Long userId, List<Long> petIds);

    boolean existsByUserIdAndPetIds(Long userId, List<Long> petIds);

    Optional<FamilyMember> findByUserIdAndPetId(Long userId, Long petId);

    void deleteAllByPetId(Long petId);
}
