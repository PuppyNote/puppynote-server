package com.puppynoteserver.pet.petTip.application.port.out.persistence;

import com.puppynoteserver.pet.petTip.domain.entity.PetTip;

import java.util.Optional;

public interface PetTipRepository {

    Optional<PetTip> findOneRandom();
}
