package com.puppynoteserver.pet.familyMembers.application.port.out.persistence;

import com.puppynoteserver.pet.familyMembers.domain.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.domain.entity.enums.FamilyMemberStatus;

import java.util.List;
import java.util.Optional;

public interface FamilyMemberRepository {

    FamilyMember save(FamilyMember familyMember);

    List<FamilyMember> findAllByPetIdAndStatus(Long petId, FamilyMemberStatus status);

    List<FamilyMember> findAllByPetIdsAndStatus(List<Long> petIds, FamilyMemberStatus status);

    List<Long> findAllPetIdsByUserId(Long userId);

    boolean existsByUserIdAndPetIds(Long userId, List<Long> petIds);

    Optional<FamilyMember> findByUserIdAndPetId(Long userId, Long petId);

    void deleteAllByPetId(Long petId);

    void deleteAllByUserIdAndPetIds(Long userId, List<Long> petIds);

    /** 내가 직접 초대했거나(내 OWNER 펫의 FAMILY) 나를 직접 초대한(내가 FAMILY인 펫의 OWNER) 유저 id들. */
    List<Long> findDirectFamilyUserIds(Long userId);
}
