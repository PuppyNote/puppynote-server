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

    @NotNull(message = "펫 ID는 필수입니다.")
    private Long petId;

    public FamilyMemberInviteServiceRequest toServiceRequest() {
        return FamilyMemberInviteServiceRequest.builder()
                .inviteeUserId(inviteeUserId)
                .petId(petId)
                .build();
    }
}
