package com.puppynoteserver.pet.pets.service.impl;

import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.entity.enums.RoleType;
import com.puppynoteserver.pet.familyMembers.service.FamilyMemberService;
import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.pet.pets.repository.PetRepository;
import com.puppynoteserver.pet.pets.service.PetWriteService;
import com.puppynoteserver.pet.pets.service.request.PetCreateServiceRequest;
import com.puppynoteserver.pet.pets.service.response.PetCreateResponse;
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
    private final FamilyMemberService familyMemberService;
    private final UserReadService userReadService;
    private final SecurityService securityService;

    @Override
    public PetCreateResponse createPet(PetCreateServiceRequest request) {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        User user = userReadService.findById(userId);

        Pet savedPet = petRepository.save(request.toEntity());

        FamilyMember familyMember = FamilyMember.of(user, savedPet, RoleType.OWNER);
        familyMemberService.save(familyMember);

        return PetCreateResponse.from(savedPet);
    }
}
