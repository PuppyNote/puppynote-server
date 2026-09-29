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
 */
public interface UserProfileReader {

    List<UserProfile> searchByEmailLike(String email, Long excludeUserId);

    Map<Long, UserProfile> findAllByIds(List<Long> userIds);
}
