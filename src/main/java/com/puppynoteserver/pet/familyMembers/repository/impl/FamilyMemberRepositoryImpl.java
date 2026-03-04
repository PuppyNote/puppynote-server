package com.puppynoteserver.pet.familyMembers.repository.impl;

import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.entity.enums.RoleType;
import com.puppynoteserver.pet.familyMembers.repository.FamilyMemberJpaRepository;
import com.puppynoteserver.pet.familyMembers.repository.FamilyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FamilyMemberRepositoryImpl implements FamilyMemberRepository {

    private final FamilyMemberJpaRepository familyMemberJpaRepository;

    @Override
    public FamilyMember save(FamilyMember familyMember) {
        return familyMemberJpaRepository.save(familyMember);
    }

    @Override
    public List<FamilyMember> findByPetIdWithUser(Long petId) {
        return familyMemberJpaRepository.findByPetIdWithUser(petId);
    }

    @Override
    public List<FamilyMember> findAllByPetIdAndStatus(Long petId, FamilyMemberStatus status) {
        return familyMemberJpaRepository.findAllByPetIdAndStatusWithUser(petId, status);
    }

    @Override
    public List<FamilyMember> findAllByPetIdsAndStatus(List<Long> petIds, FamilyMemberStatus status) {
        if (petIds.isEmpty()) {
            return List.of();
        }
        return familyMemberJpaRepository.findAllByPetIdInAndStatusWithUser(petIds, status);
    }

    @Override
    public List<Long> findAllPetIdsByUserId(Long userId) {
        return familyMemberJpaRepository.findAllByUserIdAndStatus(userId, FamilyMemberStatus.DONE)
                .stream()
                .map(fm -> fm.getId().getPetId())
                .toList();
    }

    @Override
    public List<FamilyMember> findAllOwnerPetsOf(Long userId) {
        return familyMemberJpaRepository.findAllByUserIdAndRoleAndStatusWithPet(userId, RoleType.OWNER, FamilyMemberStatus.DONE);
    }

    @Override
    public List<FamilyMember> findAllPendingByUserIdAndPetIds(Long userId, List<Long> petIds) {
        if (petIds.isEmpty()) {
            return List.of();
        }
        return familyMemberJpaRepository.findAllByUserIdAndPetIdInAndStatus(userId, petIds, FamilyMemberStatus.PENDING);
    }

    @Override
    public boolean existsByUserIdAndPetIds(Long userId, List<Long> petIds) {
        if (petIds.isEmpty()) {
            return false;
        }
        return familyMemberJpaRepository.countByUserIdAndPetIdIn(userId, petIds) > 0;
    }
}
