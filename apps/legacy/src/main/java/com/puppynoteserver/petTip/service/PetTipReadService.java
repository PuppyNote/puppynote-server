package com.puppynoteserver.petTip.service;

import com.puppynoteserver.petTip.service.response.PetTipResponse;

public interface PetTipReadService {

    PetTipResponse getRandomTip();
}
