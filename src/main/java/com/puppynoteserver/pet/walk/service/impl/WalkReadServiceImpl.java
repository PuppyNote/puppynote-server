package com.puppynoteserver.pet.walk.service.impl;

import com.puppynoteserver.pet.walk.entity.WalkPhoto;
import com.puppynoteserver.pet.walk.repository.WalkRepository;
import com.puppynoteserver.pet.walk.service.WalkReadService;
import com.puppynoteserver.pet.walk.service.response.WalkResponse;
import com.puppynoteserver.storage.enums.BucketKind;
import com.puppynoteserver.storage.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalkReadServiceImpl implements WalkReadService {

    private final WalkRepository walkRepository;
    private final S3StorageService s3StorageService;

    @Override
    public List<WalkResponse> getWalksByPetId(Long petId, LocalDate date) {
        return walkRepository.findByPetIdAndStartTimeBetweenOrderByEndTimeDesc(
                        petId,
                        date.atStartOfDay(),
                        date.plusDays(1).atStartOfDay().minusNanos(1)
                ).stream()
                .map(walk -> {
                    List<String> photoUrls = walk.getPhotos().stream()
                            .map(WalkPhoto::getImageKey)
                            .map(key -> s3StorageService.createPresignedUrl(key, BucketKind.WALK_PHOTO))
                            .toList();
                    return WalkResponse.of(walk, photoUrls);
                })
                .toList();
    }
}
