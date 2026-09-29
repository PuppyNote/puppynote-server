package com.puppynoteserver.pet.pets.application.port.out.persistence;

import com.puppynoteserver.pet.familyMembers.domain.entity.FamilyMember;
import com.puppynoteserver.pet.pets.domain.entity.Pet;

import java.util.List;
import java.util.Optional;

public interface PetRepository {

    List<Pet> findByUserId(Long userId);

    List<FamilyMember> findFamilyMembersByUserId(Long userId);

    Pet save(Pet pet);

    Optional<Pet> findById(Long petId);

    void deleteById(Long petId);
}
