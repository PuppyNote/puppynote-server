package com.puppynoteserver.user.users.service;

import com.puppynoteserver.IntegrationTestSupport;
import com.puppynoteserver.global.exception.PuppyNoteException;
import com.puppynoteserver.storage.enums.BucketKind;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import com.puppynoteserver.user.users.service.response.UserProfileResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

public class UserReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserReadService userReadService;

    @DisplayName("이메일로 사용자를 조회한다.")
    @Test
    void findByEmail_success() {
        // given
        User user = createUser("test@test.com", "password123", SnsType.NORMAL);
        User savedUser = userRepository.save(user);

        // when
        User foundUser = userReadService.findByEmail("test@test.com");

        // then
        assertAll(
                () -> assertThat(foundUser.getId()).isEqualTo(savedUser.getId()),
                () -> assertThat(foundUser.getEmail()).isEqualTo("test@test.com"),
                () -> assertThat(foundUser.getNickName()).isEqualTo(savedUser.getNickName())
        );
    }

    @DisplayName("존재하지 않는 이메일로 조회 시 예외가 발생한다.")
    @Test
    void findByEmail_notFound_throwsException() {
        // given
        // when / then
        assertThatThrownBy(() -> userReadService.findByEmail("notexist@test.com"))
                .isInstanceOf(PuppyNoteException.class)
                .hasMessageContaining("해당 회원은 존재하지 않습니다.");
    }

    @DisplayName("ID로 사용자를 조회한다.")
    @Test
    void findById_success() {
        // given
        User user = createUser("test@test.com", "password123", SnsType.NORMAL);
        User savedUser = userRepository.save(user);

        // when
        User foundUser = userReadService.findById(savedUser.getId());

        // then
        assertAll(
                () -> assertThat(foundUser.getId()).isEqualTo(savedUser.getId()),
                () -> assertThat(foundUser.getEmail()).isEqualTo("test@test.com"),
                () -> assertThat(foundUser.getNickName()).isEqualTo(savedUser.getNickName())
        );
    }

    @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다.")
    @Test
    void findById_notFound_throwsException() {
        // given
        Long nonExistentId = 999999L;

        // when / then
        assertThatThrownBy(() -> userReadService.findById(nonExistentId))
                .isInstanceOf(PuppyNoteException.class)
                .hasMessageContaining("해당 회원은 존재하지 않습니다.");
    }

    @DisplayName("내 프로필을 조회한다.")
    @Test
    void getMyProfile_success() {
        // given
        User user = User.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .nickName("테스트 닉네임")
                .profileUrl("profile-image.jpg")
                .role(com.puppynoteserver.user.users.entity.enums.Role.USER)
                .snsType(SnsType.NORMAL)
                .useYn("Y")
                .build();
        User savedUser = userRepository.save(user);

        given(securityService.getCurrentLoginUserInfo())
                .willReturn(createLoginUserInfo(savedUser.getId()));

        String expectedCloudFrontUrl = "https://cdn.example.com/profile-image.jpg";
        given(s3StorageService.getCloudFrontUrl("profile-image.jpg", BucketKind.USER_PROFILE))
                .willReturn(expectedCloudFrontUrl);

        // when
        UserProfileResponse response = userReadService.getMyProfile();

        // then
        assertAll(
                () -> assertThat(response.getUserId()).isEqualTo(savedUser.getId()),
                () -> assertThat(response.getEmail()).isEqualTo("test@test.com"),
                () -> assertThat(response.getNickName()).isEqualTo("테스트 닉네임"),
                () -> assertThat(response.getProfileUrl()).isEqualTo(expectedCloudFrontUrl)
        );
    }
}
