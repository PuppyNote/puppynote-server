package com.puppynoteserver.petTip.repository;

import com.puppynoteserver.petTip.entity.PetTip;

import java.util.Optional;

public interface PetTipRepository {

    Optional<PetTip> findOneRandom();
}
