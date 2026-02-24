package com.puppynoteserver.user.users.entity;

import com.puppynoteserver.IntegrationTestSupport;
import com.puppynoteserver.global.exception.PuppyNoteException;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserTest extends IntegrationTestSupport {

    @DisplayName("로그인 타입을 검증한다.")
    @Test
    void 로그인_타입을_검증한다() {
        User user = createUser("test@test.com", "1234", SnsType.NORMAL);
        user.checkSnsType(SnsType.NORMAL);
    }

    @DisplayName("로그인 타입을 검증시 이미 다른 타입의 회원가입 타입이면 예외가 발생한다.")
    @Test
    void 로그인_타입을_검증시_이미_다른_타입의_회원가입_타입이면_예외가_발생한다() {
        User user = createUser("test@test.com", "1234", SnsType.NORMAL);

        assertThatThrownBy(() -> user.checkSnsType(SnsType.KAKAO))
                .isInstanceOf(PuppyNoteException.class)
                .hasMessage("NORMAL");
    }

    @DisplayName("프로필 사진을 수정한다.")
    @Test
    void 프로필_사진을_수정한다() {
        User user = createUser("test@test.com", "1234", SnsType.NORMAL);
        user.updateProfileUrl("test.com");

        assertThat(user.getProfileUrl()).isEqualTo("test.com");
    }
}
