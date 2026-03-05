package com.puppynoteserver.pet.pets.service.impl;

import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.global.exception.PuppyNoteException;
import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.entity.enums.RoleType;
import com.puppynoteserver.pet.familyMembers.repository.FamilyMemberRepository;
import com.puppynoteserver.pet.familyMembers.service.FamilyMemberService;
import com.puppynoteserver.pet.petItemPurchase.repository.PetItemPurchaseRepository;
import com.puppynoteserver.pet.petItems.repository.PetItemRepository;
import com.puppynoteserver.pet.petWalkAlarms.repository.PetWalkAlarmRepository;
import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.pet.pets.repository.PetRepository;
import com.puppynoteserver.pet.pets.service.PetWriteService;
import com.puppynoteserver.pet.pets.service.request.PetCreateServiceRequest;
import com.puppynoteserver.pet.pets.service.request.PetUpdateServiceRequest;
import com.puppynoteserver.pet.pets.service.response.PetCreateResponse;
import com.puppynoteserver.pet.walk.repository.WalkRepository;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.service.UserReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PetWriteServiceImpl implements PetWriteService {


    private final PetRepository petRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyMemberService familyMemberService;
    private final PetItemPurchaseRepository petItemPurchaseRepository;
    private final PetItemRepository petItemRepository;
    private final PetWalkAlarmRepository petWalkAlarmRepository;
    private final WalkRepository walkRepository;
    private final UserReadService userReadService;
    private final SecurityService securityService;

    @Override
    public PetCreateResponse createPet(PetCreateServiceRequest request) {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        User user = userReadService.findById(userId);

        Pet savedPet = petRepository.save(request.toEntity());

        FamilyMember familyMember = FamilyMember.of(user, savedPet, RoleType.OWNER, FamilyMemberStatus.DONE);
        familyMemberService.save(familyMember);

        return PetCreateResponse.from(savedPet);
    }

    @Override
    public void updatePet(Long petId, PetUpdateServiceRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new NotFoundException("펫을 찾을 수 없습니다."));
        pet.updateInfo(request.getName(), request.getBirthDate(), request.getProfileImage());
    }

    @Override
    public void deletePet(Long petId) {
        petRepository.findById(petId)
                .orElseThrow(() -> new NotFoundException("펫을 찾을 수 없습니다."));

        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        FamilyMember familyMember = familyMemberRepository.findByUserIdAndPetId(userId, petId)
                .orElseThrow(() -> new NotFoundException("해당 펫의 가족 구성원 정보를 찾을 수 없습니다."));

        if (familyMember.getRole() != RoleType.OWNER) {
            throw new PuppyNoteException("펫 삭제는 OWNER만 가능합니다.");
        }

        // 연관 데이터 순서대로 삭제
        petItemPurchaseRepository.deleteAllByPetId(petId);
        petItemRepository.deleteAllByPetId(petId);
        walkRepository.deleteAllByPetId(petId);
        petWalkAlarmRepository.deleteAllByPetId(petId);
        familyMemberRepository.deleteAllByPetId(petId);
        petRepository.deleteById(petId);
    }
}
