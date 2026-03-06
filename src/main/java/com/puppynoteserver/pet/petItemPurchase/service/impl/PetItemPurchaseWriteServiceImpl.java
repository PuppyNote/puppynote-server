package com.puppynoteserver.pet.petItemPurchase.service.impl;

import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.pet.petItemPurchase.entity.PetItemPurchase;
import com.puppynoteserver.pet.petItemPurchase.repository.PetItemPurchaseRepository;
import com.puppynoteserver.pet.petItemPurchase.service.PetItemPurchaseWriteService;
import com.puppynoteserver.pet.petItemPurchase.service.request.PetItemPurchaseCreateServiceRequest;
import com.puppynoteserver.pet.petItemPurchase.service.response.PetItemPurchaseResponse;
import com.puppynoteserver.pet.petItems.entity.PetItem;
import com.puppynoteserver.pet.petItems.service.PetItemReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PetItemPurchaseWriteServiceImpl implements PetItemPurchaseWriteService {

    private final PetItemPurchaseRepository petItemPurchaseRepository;
    private final PetItemReadService petItemReadService;

    @Override
    public PetItemPurchaseResponse recordPurchase(PetItemPurchaseCreateServiceRequest request) {
        PetItem petItem = petItemReadService.findById(request.getPetItemId());
        PetItemPurchase saved = petItemPurchaseRepository.save(request.toEntity(petItem));
        return PetItemPurchaseResponse.of(saved);
    }

    @Override
    public void deleteAllByPetItemId(Long petItemId) {
        petItemPurchaseRepository.deleteAllByPetItemId(petItemId);
    }

    @Override
    public void deleteAllByPetId(Long petId) {
        petItemPurchaseRepository.deleteAllByPetId(petId);
    }
}
