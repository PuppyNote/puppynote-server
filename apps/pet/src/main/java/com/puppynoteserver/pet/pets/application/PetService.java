package com.puppynoteserver.pet.pets.application;

import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.global.exception.PuppyNoteException;
import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.pet.familyMembers.application.port.in.FamilyMemberFinder;
import com.puppynoteserver.pet.familyMembers.application.port.in.FamilyMemberRegister;
import com.puppynoteserver.pet.familyMembers.application.port.in.FamilyMemberRemover;
import com.puppynoteserver.pet.familyMembers.domain.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.domain.entity.enums.RoleType;
import com.puppynoteserver.pet.petItems.application.port.in.PetItemRemover;
import com.puppynoteserver.pet.petWalkAlarms.application.port.in.PetWalkAlarmRemover;
import com.puppynoteserver.pet.pets.application.port.in.PetFinder;
import com.puppynoteserver.pet.pets.application.port.in.PetRegister;
import com.puppynoteserver.pet.pets.application.port.in.PetRemover;
import com.puppynoteserver.pet.pets.application.port.in.PetUpdater;
import com.puppynoteserver.pet.pets.domain.error.PetErrorMessage;
import com.puppynoteserver.pet.pets.application.port.in.request.PetCreateServiceRequest;
import com.puppynoteserver.pet.pets.application.port.in.request.PetUpdateServiceRequest;
import com.puppynoteserver.pet.pets.application.port.in.response.PetCreateResponse;
import com.puppynoteserver.pet.pets.application.port.in.response.PetResponse;
import com.puppynoteserver.pet.pets.application.port.out.persistence.PetRepository;
import com.puppynoteserver.pet.pets.domain.entity.Pet;
import com.puppynoteserver.pet.walk.application.port.in.WalkRemover;
import com.puppynoteserver.storage.application.port.out.FileStorage;
import com.puppynoteserver.storage.enums.BucketKind;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class PetService implements PetFinder, PetRegister, PetUpdater, PetRemover {

    private final PetRepository petRepository;
    private final FamilyMemberFinder familyMemberFinder;
    private final FamilyMemberRegister familyMemberRegister;
    private final FamilyMemberRemover familyMemberRemover;
    private final PetItemRemover petItemRemover;
    private final PetWalkAlarmRemover petWalkAlarmRemover;
    private final WalkRemover walkRemover;
    private final SecurityService securityService;
    private final FileStorage fileStorage;

    @Override
    @Transactional(readOnly = true)
    public List<PetResponse> getMyPets() {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        return petRepository.findFamilyMembersByUserId(userId).stream()
                .map(fm -> PetResponse.of(
                        fm.getPet(),
                        fileStorage.getCloudFrontUrl(fm.getPet().getProfileImage(), BucketKind.PUPPY_PROFILE),
                        fm.getRole()
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Pet findById(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new NotFoundException(PetErrorMessage.PET_NOT_FOUND.getMessage()));
    }

    @Override
    public PetCreateResponse createPet(PetCreateServiceRequest request) {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();

        Pet savedPet = petRepository.save(request.toEntity());

        // 현재 유저를 OWNER로 추가
        familyMemberRegister.registerOwner(userId, savedPet);

        // 기존 가족 멤버들을 FAMILY로 추가
        familyMemberRegister.registerFamilyMembers(familyMemberFinder.findFamilyUserIds(userId), savedPet);

        return PetCreateResponse.from(savedPet);
    }

    @Override
    public void updatePet(Long petId, PetUpdateServiceRequest request) {
        Pet pet = findById(petId);
        String oldProfileImage = pet.getProfileImage();

        pet.updateInfo(request.getName(), request.getBirthDate(), request.getProfileImage(), request.getRegistrationNumber());

        // 프로필 이미지가 변경된 경우 기존 이미지 S3에서 삭제
        if (oldProfileImage != null && !Objects.equals(oldProfileImage, request.getProfileImage())) {
            fileStorage.deleteObject(oldProfileImage, BucketKind.PUPPY_PROFILE);
        }
    }

    @Override
    public void deletePet(Long petId) {
        Pet pet = findById(petId);

        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        FamilyMember familyMember = familyMemberFinder.findByUserIdAndPetId(userId, petId)
                .orElseThrow(() -> new NotFoundException(PetErrorMessage.FAMILY_MEMBER_NOT_FOUND.getMessage()));

        if (familyMember.getRole() != RoleType.OWNER) {
            throw new PuppyNoteException(PetErrorMessage.DELETE_NOT_ALLOWED.getMessage());
        }

        // 연관 데이터 순서대로 삭제 (각 서비스에서 S3 이미지도 함께 삭제)
        petItemRemover.deleteAllByPetId(petId);
        walkRemover.deleteAllByPetId(petId);
        petWalkAlarmRemover.deleteAllByPetId(petId);
        familyMemberRemover.deleteAllByPetId(petId);
        petRepository.deleteById(petId);

        // 펫 프로필 이미지 S3 삭제
        fileStorage.deleteObject(pet.getProfileImage(), BucketKind.PUPPY_PROFILE);
    }
}
