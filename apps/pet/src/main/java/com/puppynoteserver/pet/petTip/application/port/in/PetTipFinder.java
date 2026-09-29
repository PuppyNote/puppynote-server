package com.puppynoteserver.pet.petTip.application.port.in;

import com.puppynoteserver.pet.petTip.application.port.in.response.PetTipResponse;

public interface PetTipFinder {

    PetTipResponse getRandomTip();
}
