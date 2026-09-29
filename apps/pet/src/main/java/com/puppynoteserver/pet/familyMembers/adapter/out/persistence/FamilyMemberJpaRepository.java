package com.puppynoteserver.pet.familyMembers.adapter.out.persistence;

import com.puppynoteserver.pet.familyMembers.domain.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.domain.entity.FamilyMemberId;
import com.puppynoteserver.pet.familyMembers.domain.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.domain.entity.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FamilyMemberJpaRepository extends JpaRepository<FamilyMember, FamilyMemberId> {

    @Query("SELECT fm FROM FamilyMember fm WHERE fm.id.petId = :petId AND fm.status = :status")
    List<FamilyMember> findAllByPetIdAndStatus(@Param("petId") Long petId, @Param("status") FamilyMemberStatus status);

    @Query("SELECT fm FROM FamilyMember fm WHERE fm.id.petId IN :petIds AND fm.status = :status")
    List<FamilyMember> findAllByPetIdInAndStatus(@Param("petIds") List<Long> petIds, @Param("status") FamilyMemberStatus status);

    // 특정 유저가 속한 모든 펫 목록 (역할 무관, 가족 목록 조회 시 사용)
    @Query("SELECT fm FROM FamilyMember fm WHERE fm.id.userId = :userId AND fm.status = :status")
    List<FamilyMember> findAllByUserIdAndStatus(@Param("userId") Long userId, @Param("status") FamilyMemberStatus status);

    // 중복 초대 체크
    @Query("SELECT COUNT(fm) FROM FamilyMember fm WHERE fm.id.userId = :userId AND fm.id.petId IN :petIds")
    long countByUserIdAndPetIdIn(@Param("userId") Long userId, @Param("petIds") List<Long> petIds);

    Optional<FamilyMember> findByIdUserIdAndIdPetId(Long userId, Long petId);

    void deleteAllByIdPetId(Long petId);

    @Modifying
    @Query("DELETE FROM FamilyMember fm WHERE fm.id.userId = :userId AND fm.id.petId IN :petIds")
    void deleteAllByUserIdAndPetIdIn(@Param("userId") Long userId, @Param("petIds") List<Long> petIds);

    // 내가 OWNER인 펫의 DONE FAMILY 멤버 유저 id들 (내가 직접 초대한 사람들)
    @Query("SELECT DISTINCT fm.id.userId FROM FamilyMember fm " +
            "WHERE fm.id.petId IN (SELECT fm2.id.petId FROM FamilyMember fm2 WHERE fm2.id.userId = :userId AND fm2.role = :ownerRole AND fm2.status = :doneStatus) " +
            "AND fm.role = :familyRole AND fm.status = :doneStatus AND fm.id.userId <> :userId")
    List<Long> findFamilyUserIdsOnOwnedPets(@Param("userId") Long userId, @Param("ownerRole") RoleType ownerRole, @Param("familyRole") RoleType familyRole, @Param("doneStatus") FamilyMemberStatus doneStatus);

    // 내가 FAMILY인 펫의 DONE OWNER 유저 id들 (나를 직접 초대한 사람들)
    @Query("SELECT DISTINCT fm.id.userId FROM FamilyMember fm " +
            "WHERE fm.id.petId IN (SELECT fm2.id.petId FROM FamilyMember fm2 WHERE fm2.id.userId = :userId AND fm2.role = :familyRole AND fm2.status = :doneStatus) " +
            "AND fm.role = :ownerRole AND fm.status = :doneStatus")
    List<Long> findOwnerUserIdsOfJoinedPets(@Param("userId") Long userId, @Param("familyRole") RoleType familyRole, @Param("ownerRole") RoleType ownerRole, @Param("doneStatus") FamilyMemberStatus doneStatus);
}
