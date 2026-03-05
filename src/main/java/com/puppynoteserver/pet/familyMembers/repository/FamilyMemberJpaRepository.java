package com.puppynoteserver.pet.familyMembers.repository;

import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.entity.FamilyMemberId;
import com.puppynoteserver.pet.familyMembers.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.entity.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FamilyMemberJpaRepository extends JpaRepository<FamilyMember, FamilyMemberId> {

    @Query("SELECT fm FROM FamilyMember fm JOIN FETCH fm.user WHERE fm.id.petId = :petId")
    List<FamilyMember> findByPetIdWithUser(@Param("petId") Long petId);

    @Query("SELECT fm FROM FamilyMember fm JOIN FETCH fm.user WHERE fm.id.petId = :petId AND fm.status = :status")
    List<FamilyMember> findAllByPetIdAndStatusWithUser(@Param("petId") Long petId, @Param("status") FamilyMemberStatus status);

    // 특정 유저가 OWNER로 보유한 펫 목록 (초대 시 사용)
    @Query("SELECT fm FROM FamilyMember fm JOIN FETCH fm.pet WHERE fm.id.userId = :userId AND fm.role = :role AND fm.status = :status")
    List<FamilyMember> findAllByUserIdAndRoleAndStatusWithPet(@Param("userId") Long userId, @Param("role") RoleType role, @Param("status") FamilyMemberStatus status);

    // 특정 유저가 속한 모든 펫 목록 (역할 무관, 가족 목록 조회 시 사용)
    @Query("SELECT fm FROM FamilyMember fm WHERE fm.id.userId = :userId AND fm.status = :status")
    List<FamilyMember> findAllByUserIdAndStatus(@Param("userId") Long userId, @Param("status") FamilyMemberStatus status);

    // 특정 유저의 특정 펫들에 대한 레코드 (등록 시 PENDING → DONE)
    @Query("SELECT fm FROM FamilyMember fm WHERE fm.id.userId = :userId AND fm.id.petId IN :petIds AND fm.status = :status")
    List<FamilyMember> findAllByUserIdAndPetIdInAndStatus(@Param("userId") Long userId, @Param("petIds") List<Long> petIds, @Param("status") FamilyMemberStatus status);

    // 중복 초대 체크
    @Query("SELECT COUNT(fm) FROM FamilyMember fm WHERE fm.id.userId = :userId AND fm.id.petId IN :petIds")
    long countByUserIdAndPetIdIn(@Param("userId") Long userId, @Param("petIds") List<Long> petIds);

    // 여러 펫에 대한 DONE 가족 목록 조회 (가족 목록 API용)
    @Query("SELECT fm FROM FamilyMember fm JOIN FETCH fm.user WHERE fm.id.petId IN :petIds AND fm.status = :status")
    List<FamilyMember> findAllByPetIdInAndStatusWithUser(@Param("petIds") List<Long> petIds, @Param("status") FamilyMemberStatus status);

    Optional<FamilyMember> findByIdUserIdAndIdPetId(Long userId, Long petId);

    void deleteAllByIdPetId(Long petId);
}
