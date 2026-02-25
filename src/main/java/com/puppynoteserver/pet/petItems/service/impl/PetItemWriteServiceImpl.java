package com.puppynoteserver.pet.petItems.service.impl;

import com.puppynoteserver.pet.petItems.entity.PetItem;
import com.puppynoteserver.pet.petItems.repository.PetItemRepository;
import com.puppynoteserver.pet.petItems.service.PetItemWriteService;
import com.puppynoteserver.pet.petItems.service.request.PetItemCreateServiceRequest;
import com.puppynoteserver.pet.petItems.service.response.PetItemResponse;
import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.pet.pets.service.PetReadService;
import com.puppynoteserver.storage.enums.BucketKind;
import com.puppynoteserver.storage.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PetItemWriteServiceImpl implements PetItemWriteService {

    private final PetItemRepository petItemRepository;
    private final PetReadService petReadService;
    private final S3StorageService s3StorageService;

    @Override
    public PetItemResponse create(PetItemCreateServiceRequest request) {
        Pet pet = petReadService.findById(request.getPetId());
        PetItem petItem = request.toEntity(pet);
        PetItem savedItem = petItemRepository.save(petItem);

        String imageUrl = s3StorageService.createPresignedUrl(savedItem.getImageKey(), BucketKind.PET_ITEM_IMAGE);

        return PetItemResponse.of(savedItem, imageUrl, null);
    }
}
