package com.puppynoteserver.pet.familyMembers.adapter.out.persistence;

import com.puppynoteserver.pet.familyMembers.application.port.out.persistence.FamilyMemberRepository;
import com.puppynoteserver.pet.familyMembers.domain.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.domain.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.domain.entity.enums.RoleType;
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
    public List<FamilyMember> findAllByPetIdAndStatus(Long petId, FamilyMemberStatus status) {
        return familyMemberJpaRepository.findAllByPetIdAndStatus(petId, status);
    }

    @Override
    public List<FamilyMember> findAllByPetIdsAndStatus(List<Long> petIds, FamilyMemberStatus status) {
        if (petIds.isEmpty()) {
            return List.of();
        }
        return familyMemberJpaRepository.findAllByPetIdInAndStatus(petIds, status);
    }

    @Override
    public List<Long> findAllPetIdsByUserId(Long userId) {
        return familyMemberJpaRepository.findAllByUserIdAndStatus(userId, FamilyMemberStatus.DONE)
                .stream()
                .map(fm -> fm.getId().getPetId())
                .toList();
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
    public List<Long> findDirectFamilyUserIds(Long userId) {
        List<Long> familyOnMyPets = familyMemberJpaRepository.findFamilyUserIdsOnOwnedPets(
                userId, RoleType.OWNER, RoleType.FAMILY, FamilyMemberStatus.DONE);
        List<Long> ownersWhoInvitedMe = familyMemberJpaRepository.findOwnerUserIdsOfJoinedPets(
                userId, RoleType.FAMILY, RoleType.OWNER, FamilyMemberStatus.DONE);
        return Stream.concat(familyOnMyPets.stream(), ownersWhoInvitedMe.stream())
                .distinct()
                .toList();
    }
}
