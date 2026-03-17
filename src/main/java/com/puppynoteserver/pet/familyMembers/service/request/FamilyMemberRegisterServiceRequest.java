package com.puppynoteserver.pet.familyMembers.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FamilyMemberRegisterServiceRequest {

    private final Long userId;
    private final Long petId;

    @Builder
    private FamilyMemberRegisterServiceRequest(Long userId, Long petId) {
        this.userId = userId;
        this.petId = petId;
    }
}
