package com.puppynoteserver.petTip.repository.impl;

import com.puppynoteserver.petTip.entity.PetTip;
import com.puppynoteserver.petTip.repository.PetTipJpaRepository;
import com.puppynoteserver.petTip.repository.PetTipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PetTipRepositoryImpl implements PetTipRepository {

    private final PetTipJpaRepository petTipJpaRepository;

    @Override
    public Optional<PetTip> findOneRandom() {
        return petTipJpaRepository.findOneRandom();
    }
}
