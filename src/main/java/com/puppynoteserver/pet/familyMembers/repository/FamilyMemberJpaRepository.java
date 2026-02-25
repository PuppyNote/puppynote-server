package com.puppynoteserver.pet.familyMembers.repository;

import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.entity.FamilyMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyMemberJpaRepository extends JpaRepository<FamilyMember, FamilyMemberId> {
}
