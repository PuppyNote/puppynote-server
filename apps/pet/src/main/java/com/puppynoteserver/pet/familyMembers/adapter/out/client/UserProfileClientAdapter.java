package com.puppynoteserver.pet.familyMembers.adapter.out.client;

import com.puppynoteserver.contracts.user.api.UserApi;
import com.puppynoteserver.contracts.user.api.UserProfileListResponse;
import com.puppynoteserver.contracts.user.api.UserProfileResponse;
import com.puppynoteserver.global.exception.InfrastructureException;
import com.puppynoteserver.pet.familyMembers.application.port.out.UserProfile;
import com.puppynoteserver.pet.familyMembers.application.port.out.UserProfileReader;
import com.puppynoteserver.pet.familyMembers.domain.error.FamilyMemberErrorMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * [ADAPTER · OUT] user 서비스의 내부 프로필 조회 API를 호출한다.
 */
@Component
public class UserProfileClientAdapter implements UserProfileReader {

    private final RestClient userRestClient;

    public UserProfileClientAdapter(RestClient userRestClient) {
        this.userRestClient = userRestClient;
    }

    @Override
    public List<UserProfile> searchByEmailLike(String email, Long excludeUserId) {
        try {
            UserProfileListResponse response = userRestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(UserApi.PROFILES_SEARCH)
                            .queryParam(UserApi.EMAIL, email)
                            .queryParamIfPresent(UserApi.EXCLUDE_USER_ID, java.util.Optional.ofNullable(excludeUserId))
                            .build())
                    .retrieve()
                    .body(UserProfileListResponse.class);

            return toProfiles(response);
        } catch (RestClientException exception) {
            throw new InfrastructureException(FamilyMemberErrorMessage.USER_PROFILE_SEARCH_FAILED.getMessage(), exception);
        }
    }

    @Override
    public Map<Long, UserProfile> findAllByIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }
        try {
            String joined = userIds.stream().distinct().map(String::valueOf).collect(Collectors.joining(","));
            UserProfileListResponse response = userRestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(UserApi.PROFILES_QUERY).queryParam(UserApi.USER_IDS, joined).build())
                    .retrieve()
                    .body(UserProfileListResponse.class);

            return toProfiles(response).stream().collect(Collectors.toMap(UserProfile::userId, profile -> profile));
        } catch (RestClientException exception) {
            throw new InfrastructureException(FamilyMemberErrorMessage.USER_PROFILE_QUERY_FAILED.getMessage(), exception);
        }
    }

    private List<UserProfile> toProfiles(UserProfileListResponse response) {
        if (response == null || response.users() == null) {
            return List.of();
        }
        return toProfileList(response.users());
    }

    private List<UserProfile> toProfileList(Collection<UserProfileResponse> users) {
        return users.stream()
                .map(u -> new UserProfile(u.userId(), u.email(), u.nickName(), u.profileUrl()))
                .toList();
    }
}
