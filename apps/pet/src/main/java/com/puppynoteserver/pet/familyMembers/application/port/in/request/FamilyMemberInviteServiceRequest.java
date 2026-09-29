package com.puppynoteserver.pet.familyMembers.application.port.in.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FamilyMemberInviteServiceRequest {

    private final Long inviteeUserId;
    private final Long petId;

    @Builder
    private FamilyMemberInviteServiceRequest(Long inviteeUserId, Long petId) {
        this.inviteeUserId = inviteeUserId;
        this.petId = petId;
    }
}
