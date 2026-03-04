package com.puppynoteserver.pet.familyMembers.controller.request;

import com.puppynoteserver.pet.familyMembers.service.request.FamilyMemberRegisterServiceRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FamilyMemberRegisterRequest {

    @NotNull(message = "초대한 유저 ID는 필수입니다.")
    private Long inviterUserId;

    public FamilyMemberRegisterServiceRequest toServiceRequest() {
        return FamilyMemberRegisterServiceRequest.builder()
                .inviterUserId(inviterUserId)
                .build();
    }
}
