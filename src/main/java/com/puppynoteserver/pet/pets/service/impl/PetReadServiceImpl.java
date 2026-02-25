package com.puppynoteserver.pet.pets.service.impl;

import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.pet.pets.repository.PetRepository;
import com.puppynoteserver.pet.pets.service.PetReadService;
import com.puppynoteserver.pet.pets.service.response.PetResponse;
import com.puppynoteserver.storage.enums.BucketKind;
import com.puppynoteserver.storage.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetReadServiceImpl implements PetReadService {

    private final PetRepository petRepository;
    private final SecurityService securityService;
    private final S3StorageService s3StorageService;

    @Override
    public List<PetResponse> getMyPets() {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        return petRepository.findByUserId(userId).stream()
                .map(pet -> PetResponse.of(
                        pet,
                        s3StorageService.createPresignedUrl(pet.getProfileImage(), BucketKind.PUPPY_PROFILE)
                ))
                .toList();
    }

    @Override
    public Pet findById(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new NotFoundException("펫을 찾을 수 없습니다."));
    }
}
