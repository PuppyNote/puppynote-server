package com.puppynoteserver.pet.familyMembers.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FamilyMemberInviteServiceRequest {

    private final Long inviteeUserId;

    @Builder
    private FamilyMemberInviteServiceRequest(Long inviteeUserId) {
        this.inviteeUserId = inviteeUserId;
    }
}
