package com.puppynoteserver.pet.familyMembers.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FamilyMemberErrorMessage {

    PET_NOT_FOUND("해당 펫을 찾을 수 없습니다."),
    INVITE_NOT_ALLOWED("펫의 OWNER만 가족을 초대할 수 있습니다."),
    ALREADY_FAMILY("이미 가족으로 등록되어 있거나 초대 대기 중인 유저입니다."),
    INVITATION_NOT_FOUND("초대받은 내역이 없습니다."),
    ALREADY_REGISTERED("이미 가족으로 등록되어 있습니다."),
    FAMILY_MEMBER_NOT_FOUND("해당 펫의 가족 구성원 정보를 찾을 수 없습니다."),
    RELATION_NOT_FOUND("삭제할 가족 관계를 찾을 수 없습니다."),
    ONLY_FAMILY_CAN_BE_REMOVED("FAMILY 멤버만 삭제할 수 있습니다."),
    FAMILY_CANNOT_REMOVE_FAMILY("FAMILY는 FAMILY를 삭제할 수 없습니다."),
    USER_PROFILE_SEARCH_FAILED("user 서비스 프로필 검색에 실패했습니다."),
    USER_PROFILE_QUERY_FAILED("user 서비스 프로필 조회에 실패했습니다.");

    private final String message;

}
