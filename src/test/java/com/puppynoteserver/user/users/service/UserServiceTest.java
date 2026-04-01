package com.puppynoteserver.user.users.service;

import com.puppynoteserver.IntegrationTestSupport;
import com.puppynoteserver.global.exception.PuppyNoteException;
import com.puppynoteserver.storage.enums.BucketKind;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import com.puppynoteserver.user.users.service.request.SignUpServiceRequest;
import com.puppynoteserver.user.users.service.request.UserProfileUpdateServiceRequest;
import com.puppynoteserver.user.users.service.response.SignUpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class UserServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserService userService;

    @DisplayName("신규 회원가입을 하면 사용자가 저장된다.")
    @Test
    void 신규_회원가입을_하면_사용자가_저장된다() {
        // given
        SignUpServiceRequest request = SignUpServiceRequest.builder()
                .email("test@test.com")
                .password("password123")
                .nickName("테스트유저")
                .build();

        // when
        SignUpResponse response = userService.signUp(request);

        // then
        assertThat(response)
                .extracting("email", "nickName")
                .containsExactly("test@test.com", "테스트유저");

        assertThat(userRepository.existsByEmail("test@test.com")).isTrue();
    }

    @DisplayName("이미 사용 중인 이메일로 회원가입 시 예외가 발생한다.")
    @Test
    void 이미_사용_중인_이메일로_회원가입_시_예외가_발생한다() {
        // given
        User existingUser = createUser("test@test.com", "password123", SnsType.NORMAL);
        userRepository.save(existingUser);

        SignUpServiceRequest request = SignUpServiceRequest.builder()
                .email("test@test.com")
                .password("newpassword")
                .nickName("다른닉네임")
                .build();

        // when / then
        assertThatThrownBy(() -> userService.signUp(request))
                .isInstanceOf(PuppyNoteException.class)
                .hasMessageContaining("이미 사용 중인 이메일입니다.");
    }

    @DisplayName("프로필 수정 시 닉네임이 변경된다.")
    @Test
    void 프로필_수정_시_닉네임이_변경된다() {
        // given
        User user = createUser("test@test.com", "password123", SnsType.NORMAL);
        User savedUser = userRepository.save(user);

        given(securityService.getCurrentLoginUserInfo())
                .willReturn(createLoginUserInfo(savedUser.getId()));

        UserProfileUpdateServiceRequest request = UserProfileUpdateServiceRequest.builder()
                .nickName("변경된닉네임")
                .profileUrl(null)
                .build();

        // when
        userService.updateProfile(request);

        // then
        User updatedUser = userRepository.findById(savedUser.getId()).orElseThrow();
        assertThat(updatedUser.getNickName()).isEqualTo("변경된닉네임");
    }

    @DisplayName("프로필 이미지가 변경되면 기존 S3 이미지를 삭제한다.")
    @Test
    void 프로필_이미지가_변경되면_기존_S3_이미지를_삭제한다() {
        // given
        String oldProfileUrl = "old-profile-image.jpg";

        User user = User.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .nickName("테스트 닉네임")
                .profileUrl(oldProfileUrl)
                .role(com.puppynoteserver.user.users.entity.enums.Role.USER)
                .snsType(SnsType.NORMAL)
                .useYn("Y")
                .build();
        User savedUser = userRepository.save(user);

        given(securityService.getCurrentLoginUserInfo())
                .willReturn(createLoginUserInfo(savedUser.getId()));

        String newProfileUrl = "new-profile-image.jpg";
        UserProfileUpdateServiceRequest request = UserProfileUpdateServiceRequest.builder()
                .nickName("테스트 닉네임")
                .profileUrl(newProfileUrl)
                .build();

        // when
        userService.updateProfile(request);

        // then
        verify(s3StorageService).deleteObject(eq(oldProfileUrl), eq(BucketKind.USER_PROFILE));
    }

    @DisplayName("프로필 이미지가 변경되지 않으면 S3 삭제를 호출하지 않는다.")
    @Test
    void 프로필_이미지가_변경되지_않으면_S3_삭제를_호출하지_않는다() {
        // given
        String profileUrl = "same-profile-image.jpg";

        User user = User.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .nickName("테스트 닉네임")
                .profileUrl(profileUrl)
                .role(com.puppynoteserver.user.users.entity.enums.Role.USER)
                .snsType(SnsType.NORMAL)
                .useYn("Y")
                .build();
        User savedUser = userRepository.save(user);

        given(securityService.getCurrentLoginUserInfo())
                .willReturn(createLoginUserInfo(savedUser.getId()));

        UserProfileUpdateServiceRequest request = UserProfileUpdateServiceRequest.builder()
                .nickName("변경된닉네임")
                .profileUrl(profileUrl)
                .build();

        // when
        userService.updateProfile(request);

        // then
        verify(s3StorageService, never()).deleteObject(any(), any());
    }
}
