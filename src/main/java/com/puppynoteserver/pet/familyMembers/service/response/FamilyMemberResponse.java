package com.puppynoteserver.pet.familyMembers.service.response;

import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.entity.enums.RoleType;
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

    public static FamilyMemberResponse of(FamilyMember familyMember) {
        return new FamilyMemberResponse(
                familyMember.getUser().getId(),
                familyMember.getUser().getNickName(),
                familyMember.getUser().getProfileUrl(),
                familyMember.getRole(),
                familyMember.getStatus()
        );
    }
}
