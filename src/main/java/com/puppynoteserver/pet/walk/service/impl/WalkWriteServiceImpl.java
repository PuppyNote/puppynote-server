package com.puppynoteserver.pet.walk.service.impl;

import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.pet.pets.service.PetReadService;
import com.puppynoteserver.pet.walk.entity.Walk;
import com.puppynoteserver.pet.walk.repository.WalkRepository;
import com.puppynoteserver.pet.walk.service.WalkWriteService;
import com.puppynoteserver.pet.walk.service.request.WalkCreateServiceRequest;
import com.puppynoteserver.pet.walk.service.response.WalkResponse;
import com.puppynoteserver.storage.enums.BucketKind;
import com.puppynoteserver.storage.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WalkWriteServiceImpl implements WalkWriteService {

    private final WalkRepository walkRepository;
    private final PetReadService petReadService;
    private final S3StorageService s3StorageService;

    @Override
    public WalkResponse create(WalkCreateServiceRequest request) {
        Pet pet = petReadService.findById(request.getPetId());

        Walk walk = request.toEntity(pet);

        List<String> photoKeys = request.getPhotoKeys();
        if (photoKeys != null) {
            photoKeys.forEach(walk::addPhoto);
        }

        Walk savedWalk = walkRepository.save(walk);

        List<String> photoUrls = photoKeys == null ? Collections.emptyList() :
                photoKeys.stream()
                        .map(key -> s3StorageService.createPresignedUrl(key, BucketKind.WALK_PHOTO))
                        .toList();

        return WalkResponse.of(savedWalk, photoUrls);
    }
}
