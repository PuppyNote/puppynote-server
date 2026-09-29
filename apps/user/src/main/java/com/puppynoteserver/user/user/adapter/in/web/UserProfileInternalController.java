package com.puppynoteserver.user.user.adapter.in.web;

import com.puppynoteserver.contracts.user.api.UserApi;
import com.puppynoteserver.contracts.user.api.UserProfileListResponse;
import com.puppynoteserver.contracts.user.api.UserProfileResponse;
import com.puppynoteserver.user.user.application.port.out.persistence.UserRepository;
import com.puppynoteserver.user.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * [ADAPTER · IN] 다른 서비스가 user 프로필을 조회할 때 쓰는 내부 전용 API.
 *
 * <p>
 * {@code /internal/**} 경로라 인증 없이 호출 가능하다 (SecurityConfig 참고) — 사람이 아니라
 * 서비스 간 호출용이기 때문이다. contracts:user-api가 이 응답 계약을 소유한다.
 */
@RestController
@RequiredArgsConstructor
public class UserProfileInternalController {

    private final UserRepository userRepository;

    @GetMapping(UserApi.PROFILES_QUERY)
    public UserProfileListResponse getProfiles(@RequestParam(UserApi.USER_IDS) List<Long> userIds) {
        List<UserProfileResponse> profiles = userRepository.findAllByIds(userIds).stream()
                .map(this::toResponse)
                .toList();
        return new UserProfileListResponse(profiles);
    }

    @GetMapping(UserApi.PROFILES_SEARCH)
    public UserProfileListResponse searchProfiles(@RequestParam(UserApi.EMAIL) String email,
            @RequestParam(value = UserApi.EXCLUDE_USER_ID, required = false) Long excludeUserId) {
        List<UserProfileResponse> profiles = userRepository.findAllByEmailLike(email).stream()
                .filter(user -> excludeUserId == null || !Objects.equals(user.getId(), excludeUserId))
                .map(this::toResponse)
                .toList();
        return new UserProfileListResponse(profiles);
    }

    private UserProfileResponse toResponse(User user) {
        return new UserProfileResponse(user.getId(), user.getEmail(), user.getNickName(), user.getProfileUrl());
    }
}
