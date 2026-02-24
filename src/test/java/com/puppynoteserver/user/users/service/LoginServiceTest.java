package com.puppynoteserver.user.users.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.puppynoteserver.IntegrationTestSupport;
import com.puppynoteserver.global.exception.PuppyNoteException;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import com.puppynoteserver.user.users.oauth.feign.google.response.GoogleUserInfoResponse;
import com.puppynoteserver.user.users.oauth.feign.kakao.response.KakaoUserInfoResponse;
import com.puppynoteserver.user.users.service.request.LoginServiceRequest;
import com.puppynoteserver.user.users.service.request.OAuthLoginServiceRequest;
import com.puppynoteserver.user.users.service.response.LoginResponse;
import com.puppynoteserver.user.users.service.response.OAuthLoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

public class LoginServiceTest extends IntegrationTestSupport {

    @Autowired
    private LoginService loginService;

    @DisplayName("일반으로 등록된 사용자가 있을 경우 일반 로그인을 한다.")
    @Test
    void normalLoginIfUserExist() throws JsonProcessingException {
        // given
        User user = createUser("tkdrl8908@naver.com", "1234", SnsType.NORMAL);

        User savedUser = userRepository.save(user);

        LoginServiceRequest request = LoginServiceRequest.builder()
                .email(savedUser.getEmail())
                .password("1234")
                .deviceId("asdfasdfasdfsadfsf")
                .build();

        // when
        LoginResponse loginResponse = loginService.normalLogin(request);

        // then
        assertThat(loginResponse.getAccessToken()).isNotNull();
        assertThat(loginResponse.getRefreshToken()).isNotNull();
        assertThat(loginResponse.getEmail()).isEqualTo(savedUser.getEmail());
    }

    @DisplayName("SNS로 등록된 사용자가 있을 경우 일반 로그인이 되지 않는다.")
    @Test
    void normalLoginIfSnsUserExist() {
        // given
        User user = createUser("tkdrl8908@naver.com", "1234", SnsType.KAKAO);

        User savedUser = userRepository.save(user);

        LoginServiceRequest request = LoginServiceRequest.builder()
                .email(savedUser.getEmail())
                .password("1234")
                .deviceId("asdfasdfasdfsadfsf")
                .build();

        // when
        // then
        assertThrows(PuppyNoteException.class, () -> {
            loginService.normalLogin(request);
        });
    }

    @DisplayName("등록된 사용자가 없을 경우 일반 로그인을 할 시 예외를 발생시킨다.")
    @Test
    void normalLoginIfUserNotExist() {
        // given
        LoginServiceRequest request = LoginServiceRequest.builder()
                .email("tkdrl8908@naver.com")
                .password("1234")
                .deviceId("testdeviceId")
                .build();

        // when
        // then
        assertThrows(PuppyNoteException.class, () -> {
            loginService.normalLogin(request);
        });
    }

    @DisplayName("일반 로그인시 비밀번호가 틀렸을 경우 예외를 발생시킨다.")
    @Test
    void normalLoginIncorrectPassword() {
        // given
        User user = createUser("tkdrl8908@naver.com", "1234", SnsType.NORMAL);

        userRepository.save(user);

        LoginServiceRequest request = LoginServiceRequest.builder()
                .email("tkdrl8908@naver.com")
                .password("12345678")
                .deviceId("testdeviceId")
                .build();

        // when
        // then
        assertThrows(PuppyNoteException.class, () -> {
            loginService.normalLogin(request);
        });
    }

    @DisplayName("카카오 로그인을 한다.")
    @Test
    @Transactional
    void 카카오_로그인을_한다() throws JsonProcessingException {
        // given
        User user = createUser("test@test.com", "1234", SnsType.KAKAO);

        userRepository.save(user);

        given(kakaoApiFeignCall.getUserInfo(any(String.class)))
                .willReturn(
                        KakaoUserInfoResponse.builder()
                                .kakaoAccount(KakaoUserInfoResponse.KakaoAccount.builder()
                                        .email("test@test.com")
                                        .build())
                                .build()
                );

        OAuthLoginServiceRequest oAuthLoginServiceRequest = OAuthLoginServiceRequest.builder()
                .token("sadhAewofneonfoweifkpowekfkajfbdsnflksndfdsmfkl")
                .deviceId("testDeviceId")
                .pushKey("testPushKey")
                .snsType(SnsType.KAKAO)
                .build();

        OAuthLoginResponse oAuthLoginResponse = loginService.oauthLogin(oAuthLoginServiceRequest);

        assertAll(
                () -> assertThat(oAuthLoginResponse.getAccessToken()).isNotNull(),
                () -> assertThat(oAuthLoginResponse.getRefreshToken()).isNotNull(),
                () -> assertThat(oAuthLoginResponse.getEmail()).isEqualTo("test@test.com")
        );
    }

    @DisplayName("구글 로그인을 한다.")
    @Test
    @Transactional
    void 구글_로그인을_한다() throws JsonProcessingException {
        // given
        User user = createUser("test@test.com", "1234", SnsType.GOOGLE);

        userRepository.save(user);

        given(googleApiFeignCall.getUserInfo(any(String.class)))
                .willReturn(
                        GoogleUserInfoResponse.builder()
                                .email("test@test.com")
                                .build()
                );

        OAuthLoginServiceRequest oAuthLoginServiceRequest = OAuthLoginServiceRequest.builder()
                .token("sadhAewofneonfoweifkpowekfkajfbdsnflksndfdsmfkl")
                .deviceId("testDeviceId")
                .pushKey("testPushKey")
                .snsType(SnsType.GOOGLE)
                .build();

        OAuthLoginResponse oAuthLoginResponse = loginService.oauthLogin(oAuthLoginServiceRequest);

        assertAll(
                () -> assertThat(oAuthLoginResponse.getAccessToken()).isNotNull(),
                () -> assertThat(oAuthLoginResponse.getRefreshToken()).isNotNull(),
                () -> assertThat(oAuthLoginResponse.getEmail()).isEqualTo("test@test.com")
        );
    }

    @DisplayName("카카오 로그인을 할 시 이미 등록되어있는 이메일이라면 예외가 발생한다.")
    @Test
    @Transactional
    void 카카오_로그인을_할_시_이미_등록되어있는_이메일이라면_예외가_발생한다() throws JsonProcessingException {
        // given
        given(kakaoApiFeignCall.getUserInfo(any(String.class)))
                .willReturn(
                        KakaoUserInfoResponse.builder()
                                .kakaoAccount(KakaoUserInfoResponse.KakaoAccount.builder()
                                        .email("test@test.com")
                                        .build())
                                .build()
                );

        OAuthLoginServiceRequest oAuthLoginServiceRequest = OAuthLoginServiceRequest.builder()
                .token("sadhAewofneonfoweifkpowekfkajfbdsnflksndfdsmfkl")
                .deviceId("testDeviceId")
                .pushKey("testPushKey")
                .snsType(SnsType.KAKAO)
                .build();

        OAuthLoginResponse oAuthLoginResponse = loginService.oauthLogin(oAuthLoginServiceRequest);

        assertAll(
                () -> assertThat(oAuthLoginResponse.getAccessToken()).isNotNull(),
                () -> assertThat(oAuthLoginResponse.getRefreshToken()).isNotNull(),
                () -> assertThat(oAuthLoginResponse.getEmail()).isEqualTo("test@test.com")
        );
    }
}
