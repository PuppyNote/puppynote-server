package com.puppynoteserver.petTip.service.impl;

import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.petTip.repository.PetTipRepository;
import com.puppynoteserver.petTip.service.PetTipReadService;
import com.puppynoteserver.petTip.service.response.PetTipResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetTipReadServiceImpl implements PetTipReadService {

    private final PetTipRepository petTipRepository;

    @Override
    public PetTipResponse getRandomTip() {
        return petTipRepository.findOneRandom()
                .map(PetTipResponse::of)
                .orElseThrow(() -> new NotFoundException("등록된 팁이 없습니다."));
    }
}
