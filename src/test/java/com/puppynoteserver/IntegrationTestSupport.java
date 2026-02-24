package com.puppynoteserver;

import com.puppynoteserver.global.config.FCMConfig;
import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.jwt.dto.LoginUserInfo;
import com.puppynoteserver.user.push.entity.Push;
import com.puppynoteserver.user.push.repository.PushRepository;
import com.puppynoteserver.user.refreshToken.repository.RefreshTokenRepository;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.entity.enums.Role;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import com.puppynoteserver.user.users.oauth.feign.google.GoogleApiFeignCall;
import com.puppynoteserver.user.users.oauth.feign.kakao.KakaoApiFeignCall;
import com.puppynoteserver.user.users.repository.UserJpaRepository;
import com.puppynoteserver.user.users.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationTestSupport {

    @MockitoBean
    protected SecurityService securityService;
    @MockitoBean
    protected KakaoApiFeignCall kakaoApiFeignCall;
    @MockitoBean
    protected GoogleApiFeignCall googleApiFeignCall;
    @MockitoBean
    protected FCMConfig fcmConfig;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected UserJpaRepository userJpaRepository;
    @Autowired
    protected RefreshTokenRepository refreshTokenRepository;
    @Autowired
    protected PushRepository pushRepository;
    @MockitoBean
    protected MongoTemplate mongoTemplate;

    @AfterEach
    public void tearDown() {
        refreshTokenRepository.deleteAllInBatch();
        pushRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    protected User createUser(String email, String password, SnsType snsType) {
        return User.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
                .nickName("테스트 닉네임")
                .role(Role.USER)
                .snsType(snsType)
                .useYn("Y")
                .build();
    }

    protected LoginUserInfo createLoginUserInfo(Long userId) {
        return LoginUserInfo.builder()
                .userId(userId)
                .build();
    }

    protected Push createPush(User user) {
        return Push.builder()
                .user(user)
                .deviceId("testdeviceId")
                .pushToken("testPushToken")
                .build();
    }

}
