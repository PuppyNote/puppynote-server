package com.puppynoteserver.pet.pets.repository.impl;

import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.pet.pets.repository.PetJpaRepository;
import com.puppynoteserver.pet.pets.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PetRepositoryImpl implements PetRepository {

    private final PetJpaRepository petJpaRepository;

    @Override
    public List<Pet> findByUserId(Long userId) {
        return petJpaRepository.findByUserId(userId);
    }
}
