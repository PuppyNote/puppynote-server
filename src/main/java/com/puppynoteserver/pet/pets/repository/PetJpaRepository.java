package com.puppynoteserver.pet.pets.repository;

import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.pets.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PetJpaRepository extends JpaRepository<Pet, Long> {

    @Query("SELECT p FROM Pet p INNER JOIN FamilyMember fm ON fm.pet = p WHERE fm.user.id = :userId and fm.status = 'DONE'")
    List<Pet> findByUserId(@Param("userId") Long userId);

    @Query("SELECT fm FROM FamilyMember fm JOIN FETCH fm.pet WHERE fm.user.id = :userId AND fm.status = 'DONE'")
    List<FamilyMember> findFamilyMembersByUserId(@Param("userId") Long userId);
}
