package com.puppynoteserver.pet.petTip.application;

import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.pet.petTip.application.port.in.PetTipFinder;
import com.puppynoteserver.pet.petTip.application.port.in.response.PetTipResponse;
import com.puppynoteserver.pet.petTip.application.port.out.persistence.PetTipRepository;
import com.puppynoteserver.pet.petTip.domain.error.PetTipErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetTipService implements PetTipFinder {

    private final PetTipRepository petTipRepository;

    @Override
    public PetTipResponse getRandomTip() {
        return petTipRepository.findOneRandom()
                .map(PetTipResponse::of)
                .orElseThrow(() -> new NotFoundException(PetTipErrorMessage.TIP_NOT_FOUND.getMessage()));
    }
}
