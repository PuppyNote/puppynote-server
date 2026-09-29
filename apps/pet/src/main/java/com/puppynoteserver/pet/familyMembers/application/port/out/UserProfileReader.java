package com.puppynoteserver.pet.familyMembers.application.port.out;

import java.util.List;
import java.util.Map;

/**
 * [APPLICATION · OUT PORT] user 서비스에서 프로필(이메일/닉네임/프로필이미지)을 읽어온다.
 *
 * <p>
 * user가 이제 별도 서비스라 여기서 User 엔티티를 직접 조회할 수 없다. 이메일 검색(searchByEmailLike)과
 * 가족 목록 표시(findAllByIds)에 실제 프로필 데이터가 필요해서 포트로 뽑았다 —
 * chatplanet-server의 chatting이 UserProfileReader로 user 서비스를 호출하는 것과 같은 자리다.
 *
 * <p>
 * ⚠️ <b>구현체(어댑터)가 아직 없다.</b> user 서비스가 이 조회를 위한 내부 API(REST 등)를 아직
 * 노출하지 않아서다. 컴파일은 되지만(인터페이스라 구현 없이도 컴파일됨), 스프링 컨텍스트는 이
 * 인터페이스를 구현하는 빈이 없으면 뜨지 않는다 — user 서비스에 조회 API를 추가하고
 * contracts:user-api 같은 계약 모듈 + RestClient 어댑터를 붙이는 게 다음 작업이다.
 */
public interface UserProfileReader {

    List<UserProfile> searchByEmailLike(String email, Long excludeUserId);

    Map<Long, UserProfile> findAllByIds(List<Long> userIds);

    record UserProfile(Long userId, String email, String nickName, String profileUrl) {
    }
}
