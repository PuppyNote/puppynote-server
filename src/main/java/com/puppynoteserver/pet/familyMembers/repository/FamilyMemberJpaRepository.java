package com.puppynoteserver.pet.familyMembers.repository;

import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.entity.FamilyMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FamilyMemberJpaRepository extends JpaRepository<FamilyMember, FamilyMemberId> {

    @Query("SELECT fm FROM FamilyMember fm JOIN FETCH fm.user WHERE fm.id.petId = :petId")
    List<FamilyMember> findByPetIdWithUser(@Param("petId") Long petId);
}
