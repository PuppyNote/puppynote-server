package com.puppynoteserver.contracts.user.api;

/**
 * user 서비스 내부 API 경로/파라미터 이름. 발행자(apps/user)와 소비자(다른 서비스들)가 같은 상수를
 * 쓰게 계약 모듈이 소유한다 — 양쪽에 문자열을 따로 적어두면 오타 하나가 조용한 장애가 된다.
 */
public final class UserApi {

    /** userIds로 프로필을 모아 조회한다. 쿼리 파라미터 {@code userIds}는 콤마로 구분된 ID 목록. */
    public static final String PROFILES_QUERY = "/internal/v1/users/profiles";

    /** 이메일로 프로필을 검색한다. 쿼리 파라미터 {@code email}, {@code excludeUserId}(선택). */
    public static final String PROFILES_SEARCH = "/internal/v1/users/profiles/search";

    public static final String USER_IDS = "userIds";
    public static final String EMAIL = "email";
    public static final String EXCLUDE_USER_ID = "excludeUserId";

    private UserApi() {
    }
}
