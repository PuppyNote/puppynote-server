package com.puppynoteserver.pet.familyMembers.controller.request;

import com.puppynoteserver.pet.familyMembers.service.request.FamilyMemberRegisterServiceRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FamilyMemberRegisterRequest {

    @NotNull(message = "유저 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "펫 ID는 필수입니다.")
    private Long petId;

    public FamilyMemberRegisterServiceRequest toServiceRequest() {
        return FamilyMemberRegisterServiceRequest.builder()
                .userId(userId)
                .petId(petId)
                .build();
    }
}
