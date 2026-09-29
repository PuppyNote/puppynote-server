package com.puppynoteserver.pet.pets.adapter.out.persistence;

import com.puppynoteserver.pet.familyMembers.domain.entity.FamilyMember;
import com.puppynoteserver.pet.pets.application.port.out.persistence.PetRepository;
import com.puppynoteserver.pet.pets.domain.entity.Pet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PetRepositoryImpl implements PetRepository {

    private final PetJpaRepository petJpaRepository;

    @Override
    public List<Pet> findByUserId(Long userId) {
        return petJpaRepository.findByUserId(userId);
    }

    @Override
    public List<FamilyMember> findFamilyMembersByUserId(Long userId) {
        return petJpaRepository.findFamilyMembersByUserId(userId);
    }

    @Override
    public Pet save(Pet pet) {
        return petJpaRepository.save(pet);
    }

    @Override
    public Optional<Pet> findById(Long petId) {
        return petJpaRepository.findById(petId);
    }

    @Override
    public void deleteById(Long petId) {
        petJpaRepository.deleteById(petId);
    }
}
