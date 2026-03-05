package com.puppynoteserver.pet.petItems.service.impl;

import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.pet.petItemPurchase.repository.PetItemPurchaseRepository;
import com.puppynoteserver.pet.petItems.entity.PetItem;
import com.puppynoteserver.pet.petItems.repository.PetItemRepository;
import com.puppynoteserver.pet.petItems.service.PetItemWriteService;
import com.puppynoteserver.pet.petItems.service.request.PetItemCreateServiceRequest;
import com.puppynoteserver.pet.petItems.service.request.PetItemUpdateServiceRequest;
import com.puppynoteserver.pet.petItems.service.response.PetItemResponse;
import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.pet.pets.service.PetReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PetItemWriteServiceImpl implements PetItemWriteService {

    private final PetItemRepository petItemRepository;
    private final PetItemPurchaseRepository petItemPurchaseRepository;
    private final PetReadService petReadService;

    @Override
    public PetItemResponse create(PetItemCreateServiceRequest request) {
        Pet pet = petReadService.findById(request.getPetId());
        PetItem petItem = request.toEntity(pet);
        PetItem savedItem = petItemRepository.save(petItem);

        return PetItemResponse.of(savedItem, null, null);
    }

    @Override
    public PetItemResponse update(Long petItemId, PetItemUpdateServiceRequest request) {
        PetItem petItem = petItemRepository.findById(petItemId)
                .orElseThrow(() -> new NotFoundException("용품 정보를 찾을 수 없습니다."));

        petItem.update(request.getName(), request.getCategory(),
                request.getPurchaseCycleDays(), request.getPurchaseUrl(), request.getImageKey());

        return PetItemResponse.of(petItem, null, null);
    }

    @Override
    public void delete(Long petItemId) {
        if (petItemRepository.findById(petItemId).isEmpty()) {
            throw new NotFoundException("용품 정보를 찾을 수 없습니다.");
        }
        petItemPurchaseRepository.deleteAllByPetItemId(petItemId);
        petItemRepository.deleteById(petItemId);
    }
}
