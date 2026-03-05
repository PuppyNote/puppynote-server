package com.puppynoteserver.pet.pets.service.response;

import com.puppynoteserver.pet.familyMembers.entity.enums.RoleType;
import com.puppynoteserver.pet.pets.entity.Pet;
import lombok.Getter;

@Getter
public class PetResponse {

    private final Long petId;
    private final String petName;
    private final String petProfileUrl;
    private final RoleType roleType;

    private PetResponse(Long petId, String petName, String petProfileUrl, RoleType roleType) {
        this.petId = petId;
        this.petName = petName;
        this.petProfileUrl = petProfileUrl;
        this.roleType = roleType;
    }

    public static PetResponse of(Pet pet, String petProfileUrl, RoleType roleType) {
        return new PetResponse(pet.getId(), pet.getName(), petProfileUrl, roleType);
    }
}
