package com.puppynoteserver.pet.familyMembers.controller.request;

import com.puppynoteserver.pet.familyMembers.service.request.FamilyMemberInviteServiceRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FamilyMemberInviteRequest {

    @NotNull(message = "초대할 유저 ID는 필수입니다.")
    private Long inviteeUserId;

    public FamilyMemberInviteServiceRequest toServiceRequest() {
        return FamilyMemberInviteServiceRequest.builder()
                .inviteeUserId(inviteeUserId)
                .build();
    }
}
