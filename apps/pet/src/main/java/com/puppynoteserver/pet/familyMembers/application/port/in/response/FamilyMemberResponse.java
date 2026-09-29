package com.puppynoteserver.pet.familyMembers.application.port.in.response;

import com.puppynoteserver.pet.familyMembers.domain.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.domain.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.domain.entity.enums.RoleType;
import lombok.Getter;

@Getter
public class FamilyMemberResponse {

    private final Long userId;
    private final String nickName;
    private final String profileUrl;
    private final RoleType role;
    private final FamilyMemberStatus status;

    private FamilyMemberResponse(Long userId, String nickName, String profileUrl, RoleType role, FamilyMemberStatus status) {
        this.userId = userId;
        this.nickName = nickName;
        this.profileUrl = profileUrl;
        this.role = role;
        this.status = status;
    }

    public static FamilyMemberResponse of(FamilyMember familyMember, String nickName, String presignedProfileUrl) {
        return new FamilyMemberResponse(
                familyMember.getUserId(),
                nickName,
                presignedProfileUrl,
                familyMember.getRole(),
                familyMember.getStatus()
        );
    }
}
