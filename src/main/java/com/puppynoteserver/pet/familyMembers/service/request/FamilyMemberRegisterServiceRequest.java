package com.puppynoteserver.pet.familyMembers.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FamilyMemberRegisterServiceRequest {

    private final Long inviterUserId;

    @Builder
    private FamilyMemberRegisterServiceRequest(Long inviterUserId) {
        this.inviterUserId = inviterUserId;
    }
}
