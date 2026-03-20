package com.puppynoteserver.pet.pets.service.impl;

import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.global.exception.PuppyNoteException;
import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.entity.enums.RoleType;
import com.puppynoteserver.pet.familyMembers.service.FamilyMemberReadService;
import com.puppynoteserver.pet.familyMembers.service.FamilyMemberService;
import com.puppynoteserver.pet.familyMembers.service.FamilyMemberWriteService;
import com.puppynoteserver.pet.petItems.service.PetItemWriteService;
import com.puppynoteserver.pet.petWalkAlarms.service.PetWalkAlarmWriteService;
import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.pet.pets.repository.PetRepository;
import com.puppynoteserver.pet.pets.service.PetReadService;
import com.puppynoteserver.pet.pets.service.PetWriteService;
import com.puppynoteserver.pet.pets.service.request.PetCreateServiceRequest;
import com.puppynoteserver.pet.pets.service.request.PetUpdateServiceRequest;
import com.puppynoteserver.pet.pets.service.response.PetCreateResponse;
import com.puppynoteserver.pet.walk.service.WalkWriteService;
import com.puppynoteserver.storage.enums.BucketKind;
import com.puppynoteserver.storage.service.S3StorageService;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.service.UserReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class PetWriteServiceImpl implements PetWriteService {

    private final PetRepository petRepository;
    private final PetReadService petReadService;
    private final FamilyMemberService familyMemberService;
    private final FamilyMemberWriteService familyMemberWriteService;
    private final FamilyMemberReadService familyMemberReadService;
    private final PetItemWriteService petItemWriteService;
    private final PetWalkAlarmWriteService petWalkAlarmWriteService;
    private final WalkWriteService walkWriteService;
    private final UserReadService userReadService;
    private final SecurityService securityService;
    private final S3StorageService s3StorageService;

    @Override
    public PetCreateResponse createPet(PetCreateServiceRequest request) {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        User user = userReadService.findById(userId);

        Pet savedPet = petRepository.save(request.toEntity());

        // 현재 유저를 OWNER로 추가
        familyMemberService.save(FamilyMember.of(user, savedPet, RoleType.OWNER, FamilyMemberStatus.DONE));

        // 기존 가족 멤버들을 FAMILY로 추가
        familyMemberReadService.findFamilyUsers(userId)
                .forEach(familyUser ->
                        familyMemberService.save(FamilyMember.of(familyUser, savedPet, RoleType.FAMILY, FamilyMemberStatus.DONE)));

        return PetCreateResponse.from(savedPet);
    }

    @Override
    public void updatePet(Long petId, PetUpdateServiceRequest request) {
        Pet pet = petReadService.findById(petId);
        String oldProfileImage = pet.getProfileImage();

        pet.updateInfo(request.getName(), request.getBirthDate(), request.getProfileImage(), request.getRegistrationNumber());

        // 프로필 이미지가 변경된 경우 기존 이미지 S3에서 삭제
        if (oldProfileImage != null && !Objects.equals(oldProfileImage, request.getProfileImage())) {
            s3StorageService.deleteObject(oldProfileImage, BucketKind.PUPPY_PROFILE);
        }
    }

    @Override
    public void deletePet(Long petId) {
        Pet pet = petReadService.findById(petId);

        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        FamilyMember familyMember = familyMemberService.findByUserIdAndPetId(userId, petId)
                .orElseThrow(() -> new NotFoundException("해당 펫의 가족 구성원 정보를 찾을 수 없습니다."));

        if (familyMember.getRole() != RoleType.OWNER) {
            throw new PuppyNoteException("펫 삭제는 OWNER만 가능합니다.");
        }

        // 연관 데이터 순서대로 삭제 (각 서비스에서 S3 이미지도 함께 삭제)
        petItemWriteService.deleteAllByPetId(petId);
        walkWriteService.deleteAllByPetId(petId);
        petWalkAlarmWriteService.deleteAllByPetId(petId);
        familyMemberWriteService.deleteAllByPetId(petId);
        petRepository.deleteById(petId);

        // 펫 프로필 이미지 S3 삭제
        s3StorageService.deleteObject(pet.getProfileImage(), BucketKind.PUPPY_PROFILE);
    }
}
