package com.puppynoteserver.pet.petItemPurchase.service.impl;

import com.puppynoteserver.pet.petItemPurchase.repository.PetItemPurchaseRepository;
import com.puppynoteserver.pet.petItemPurchase.service.PetItemPurchaseReadService;
import com.puppynoteserver.pet.petItemPurchase.service.response.PetItemPurchaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetItemPurchaseReadServiceImpl implements PetItemPurchaseReadService {

    private final PetItemPurchaseRepository petItemPurchaseRepository;

    @Override
    public List<PetItemPurchaseResponse> getPurchaseHistory(Long petItemId) {
        return petItemPurchaseRepository.findAllByPetItemId(petItemId).stream()
                .map(PetItemPurchaseResponse::of)
                .toList();
    }
}
