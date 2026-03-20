package com.puppynoteserver.pet.petItems.service.impl;

import com.puppynoteserver.pet.petItemPurchase.service.PetItemPurchaseWriteService;
import com.puppynoteserver.pet.petItems.entity.PetItem;
import com.puppynoteserver.pet.petItems.repository.PetItemRepository;
import com.puppynoteserver.pet.petItems.service.PetItemReadService;
import com.puppynoteserver.pet.petItems.service.PetItemWriteService;
import com.puppynoteserver.pet.petItems.service.request.PetItemCreateServiceRequest;
import com.puppynoteserver.pet.petItems.service.request.PetItemUpdateServiceRequest;
import com.puppynoteserver.pet.petItems.service.response.PetItemResponse;
import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.pet.pets.service.PetReadService;
import com.puppynoteserver.storage.enums.BucketKind;
import com.puppynoteserver.storage.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class PetItemWriteServiceImpl implements PetItemWriteService {

    private final PetItemRepository petItemRepository;
    private final PetItemReadService petItemReadService;
    private final PetItemPurchaseWriteService petItemPurchaseWriteService;
    private final PetReadService petReadService;
    private final S3StorageService s3StorageService;

    @Override
    public PetItemResponse create(PetItemCreateServiceRequest request) {
        Pet pet = petReadService.findById(request.getPetId());
        PetItem petItem = request.toEntity(pet);
        PetItem savedItem = petItemRepository.save(petItem);

        return PetItemResponse.of(savedItem, null, null);
    }

    @Override
    public PetItemResponse update(Long petItemId, PetItemUpdateServiceRequest request) {
        PetItem petItem = petItemReadService.findById(petItemId);
        String oldImageKey = petItem.getImageKey();

        petItem.update(request.getName(), request.getCategory(),
                request.getPurchaseCycleDays(), request.getPurchaseUrl(), request.getImageKey());

        // 이미지가 변경된 경우 기존 이미지 S3에서 삭제
        if (oldImageKey != null && !Objects.equals(oldImageKey, request.getImageKey())) {
            s3StorageService.deleteObject(oldImageKey, BucketKind.PET_ITEM_PHOTO);
        }

        return PetItemResponse.of(petItem, null, null);
    }

    @Override
    public void delete(Long petItemId) {
        PetItem petItem = petItemReadService.findById(petItemId);
        petItemPurchaseWriteService.deleteAllByPetItemId(petItemId);
        petItemRepository.deleteById(petItemId);

        s3StorageService.deleteObject(petItem.getImageKey(), BucketKind.PET_ITEM_PHOTO);
    }

    @Override
    public void deleteAllByPetId(Long petId) {
        List<String> imageKeys = petItemRepository.findByPetId(petId).stream()
                .map(PetItem::getImageKey)
                .filter(Objects::nonNull)
                .toList();

        petItemPurchaseWriteService.deleteAllByPetId(petId);
        petItemRepository.deleteAllByPetId(petId);

        s3StorageService.deleteObjects(imageKeys, BucketKind.PET_ITEM_PHOTO);
    }
}
