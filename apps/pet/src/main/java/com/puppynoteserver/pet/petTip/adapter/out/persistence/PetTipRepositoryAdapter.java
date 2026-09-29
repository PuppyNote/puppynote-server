package com.puppynoteserver.pet.petTip.adapter.out.persistence;

import com.puppynoteserver.pet.petTip.application.port.out.persistence.PetTipRepository;
import com.puppynoteserver.pet.petTip.domain.entity.PetTip;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PetTipRepositoryAdapter implements PetTipRepository {

    private final PetTipJpaRepository petTipJpaRepository;

    @Override
    public Optional<PetTip> findOneRandom() {
        return petTipJpaRepository.findOneRandom();
    }
}
