package com.puppynoteserver.home.service.impl;

import com.puppynoteserver.home.service.HomeReadService;
import com.puppynoteserver.home.service.response.HomeResponse;
import com.puppynoteserver.pet.petItems.service.PetItemReadService;
import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.pet.pets.service.PetReadService;
import com.puppynoteserver.pet.walk.service.WalkReadService;
import com.puppynoteserver.storage.enums.BucketKind;
import com.puppynoteserver.storage.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeReadServiceImpl implements HomeReadService {

    private final PetReadService petReadService;
    private final WalkReadService walkReadService;
    private final PetItemReadService petItemReadService;
    private final S3StorageService s3StorageService;

    @Override
    public HomeResponse getHomeInfo(Long petId) {
        Pet pet = petReadService.findById(petId);

        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(6);

        long recentWalkCount = walkReadService.countRecentWalks(petId, sevenDaysAgo, today);
        long petItemCount = petItemReadService.countItemsByPetId(petId);

        String petProfileImageUrl = s3StorageService.createPresignedUrl(pet.getProfileImage(), BucketKind.PUPPY_PROFILE);

        return HomeResponse.of(pet.getName(), petProfileImageUrl, recentWalkCount, petItemCount);
    }
}
