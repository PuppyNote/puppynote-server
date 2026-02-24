package com.puppynoteserver.pet.pets.repository;

import com.puppynoteserver.pet.pets.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PetJpaRepository extends JpaRepository<Pet, Long> {

    @Query("SELECT p FROM Pet p INNER JOIN FamilyMember fm ON fm.pet = p WHERE fm.user.id = :userId")
    List<Pet> findByUserId(@Param("userId") Long userId);
}
