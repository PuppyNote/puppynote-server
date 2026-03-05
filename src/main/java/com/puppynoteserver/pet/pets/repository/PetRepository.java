package com.puppynoteserver.pet.pets.repository;

import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.pets.entity.Pet;

import java.util.List;
import java.util.Optional;

public interface PetRepository {

    List<Pet> findByUserId(Long userId);

    List<FamilyMember> findFamilyMembersByUserId(Long userId);

    Pet save(Pet pet);

    Optional<Pet> findById(Long petId);
}
