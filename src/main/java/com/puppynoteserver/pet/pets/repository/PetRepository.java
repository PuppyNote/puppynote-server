package com.puppynoteserver.pet.pets.repository;

import com.puppynoteserver.pet.pets.entity.Pet;

import java.util.List;

public interface PetRepository {

    List<Pet> findByUserId(Long userId);

    Pet save(Pet pet);
}
