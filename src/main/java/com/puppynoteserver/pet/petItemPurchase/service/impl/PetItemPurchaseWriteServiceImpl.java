package com.puppynoteserver.pet.petItemPurchase.service.impl;

import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.pet.petItemPurchase.entity.PetItemPurchase;
import com.puppynoteserver.pet.petItemPurchase.repository.PetItemPurchaseRepository;
import com.puppynoteserver.pet.petItemPurchase.service.PetItemPurchaseWriteService;
import com.puppynoteserver.pet.petItemPurchase.service.request.PetItemPurchaseCreateServiceRequest;
import com.puppynoteserver.pet.petItemPurchase.service.response.PetItemPurchaseResponse;
import com.puppynoteserver.pet.petItems.entity.PetItem;
import com.puppynoteserver.pet.petItems.repository.PetItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PetItemPurchaseWriteServiceImpl implements PetItemPurchaseWriteService {

    private final PetItemPurchaseRepository petItemPurchaseRepository;
    private final PetItemRepository petItemRepository;

    @Override
    public PetItemPurchaseResponse recordPurchase(PetItemPurchaseCreateServiceRequest request) {
        PetItem petItem = petItemRepository.findById(request.getPetItemId())
                .orElseThrow(() -> new NotFoundException("용품 정보를 찾을 수 없습니다."));
        PetItemPurchase saved = petItemPurchaseRepository.save(request.toEntity(petItem));
        return PetItemPurchaseResponse.of(saved);
    }
}
