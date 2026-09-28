package com.puppynoteserver.pet.familyMembers.repository.impl;

import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.entity.enums.RoleType;
import com.puppynoteserver.pet.familyMembers.repository.FamilyMemberJpaRepository;
import com.puppynoteserver.pet.familyMembers.repository.FamilyMemberRepository;
import com.puppynoteserver.user.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
    public List<FamilyMember> findAllByPetIdsWithUser(List<Long> petIds) {
        if (petIds.isEmpty()) return List.of();
        return familyMemberJpaRepository.findAllByPetIdInWithUser(petIds);
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

    @Override
    public Optional<FamilyMember> findByUserIdAndPetId(Long userId, Long petId) {
        return familyMemberJpaRepository.findByIdUserIdAndIdPetId(userId, petId);
    }

    @Override
    public void deleteAllByPetId(Long petId) {
        familyMemberJpaRepository.deleteAllByIdPetId(petId);
    }

    @Override
    public void deleteAllByUserIdAndPetIds(Long userId, List<Long> petIds) {
        if (petIds.isEmpty()) {
            return;
        }
        familyMemberJpaRepository.deleteAllByUserIdAndPetIdIn(userId, petIds);
    }

    @Override
    public List<User> findDirectFamilyUsers(Long userId) {
        // 내가 직접 초대한 사람들 (내 OWNER 펫의 FAMILY 멤버)
        List<User> familyOnMyPets = familyMemberJpaRepository.findFamilyUsersOnOwnedPets(
                userId, RoleType.OWNER, RoleType.FAMILY, FamilyMemberStatus.DONE);
        // 나를 직접 초대한 사람들 (내가 FAMILY인 펫의 OWNER)
        List<User> ownersWhoInvitedMe = familyMemberJpaRepository.findOwnerUsersOfJoinedPets(
                userId, RoleType.FAMILY, RoleType.OWNER, FamilyMemberStatus.DONE);
        return Stream.concat(familyOnMyPets.stream(), ownersWhoInvitedMe.stream())
                .distinct()
                .toList();
    }
}
