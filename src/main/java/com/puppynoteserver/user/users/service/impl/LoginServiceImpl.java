package com.puppynoteserver.user.users.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.puppynoteserver.global.exception.PuppyNoteException;
import com.puppynoteserver.jwt.JwtTokenGenerator;
import com.puppynoteserver.jwt.dto.JwtToken;
import com.puppynoteserver.jwt.dto.LoginUserInfo;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.entity.enums.Role;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import com.puppynoteserver.user.users.oauth.client.OAuthApiClient;
import com.puppynoteserver.user.users.repository.UserRepository;
import com.puppynoteserver.user.users.service.LoginService;
import com.puppynoteserver.user.users.service.UserReadService;
import com.puppynoteserver.user.refreshToken.entity.RefreshToken;
import com.puppynoteserver.user.refreshToken.service.RefreshTokenReadService;
import com.puppynoteserver.user.users.service.request.LoginServiceRequest;
import com.puppynoteserver.user.users.service.request.OAuthLoginServiceRequest;
import com.puppynoteserver.user.users.service.request.TokenRefreshServiceRequest;
import com.puppynoteserver.user.users.service.response.LoginResponse;
import com.puppynoteserver.user.users.service.response.OAuthLoginResponse;
import com.puppynoteserver.user.users.service.response.TokenRefreshResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
public class LoginServiceImpl implements LoginService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final Map<SnsType, OAuthApiClient> clients;

    private final UserRepository userRepository;
    private final UserReadService userReadService;
    private final RefreshTokenReadService refreshTokenReadService;

    public LoginServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenGenerator jwtTokenGenerator,
                            List<OAuthApiClient> clients, UserRepository userRepository, UserReadService userReadService,
                            RefreshTokenReadService refreshTokenReadService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.clients = clients.stream().collect(
                Collectors.toUnmodifiableMap(OAuthApiClient::oAuthSnsType, Function.identity())
        );
        this.userReadService = userReadService;
        this.refreshTokenReadService = refreshTokenReadService;
    }

    @Override
    public LoginResponse normalLogin(LoginServiceRequest loginServiceRequest) throws JsonProcessingException {
        User user = userReadService.findByEmail(loginServiceRequest.getEmail());     //1. 회원조회
        user.checkSnsType(SnsType.NORMAL);                                     //SNS가입여부확인

        if (!bCryptPasswordEncoder.matches(loginServiceRequest.getPassword(), user.getPassword())) {
            throw new PuppyNoteException("아이디 또는 패스워드가 일치하지 않습니다.");
        } //3. 비밀번호 체크

        JwtToken jwtToken = setJwtTokenPushKey(user, loginServiceRequest.getDeviceId(),
                loginServiceRequest.getPushKey());

        return LoginResponse.of(user, jwtToken);
    }

    @Override
    public OAuthLoginResponse oauthLogin(OAuthLoginServiceRequest oAuthLoginServiceRequest) throws
            JsonProcessingException {
        SnsType snsType = oAuthLoginServiceRequest.getSnsType();

        OAuthApiClient client = clients.get(snsType);
        Optional.ofNullable(client).orElseThrow(() -> new PuppyNoteException("존재하지않는 로그인방식입니다."));

        String email = client.getEmail(oAuthLoginServiceRequest.getToken());

        // Optional을 사용하여 트랜잭션 문제 해결
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(email)
                                .snsType(snsType)
                                .role(Role.USER)
                                .useYn("Y")
                                .build()
                ));

        user.checkSnsType(snsType);              //SNS가입여부확인

        JwtToken jwtToken = setJwtTokenPushKey(user, oAuthLoginServiceRequest.getDeviceId(),
                oAuthLoginServiceRequest.getPushKey());

        return OAuthLoginResponse.of(user, jwtToken);
    }

    @Override
    public TokenRefreshResponse refresh(TokenRefreshServiceRequest request) {
        // 1. DB에 저장된 refreshToken인지 검증
        RefreshToken storedToken = refreshTokenReadService.findByRefreshToken(request.getRefreshToken());

        // 2. JWT 서명 및 만료 검증 후 새 토큰 발급
        JwtToken jwtToken = jwtTokenGenerator.generateJwtToken(request.getRefreshToken());

        // 3. DB의 refreshToken을 새 토큰으로 업데이트 (토큰 로테이션)
        storedToken.updateRefreshToken(jwtToken.getRefreshToken());

        return TokenRefreshResponse.from(jwtToken);
    }

    private JwtToken setJwtTokenPushKey(User user, String deviceId, String pushKey) throws JsonProcessingException {
        LoginUserInfo userInfo = LoginUserInfo.of(user.getId());
        JwtToken jwtToken = jwtTokenGenerator.generate(userInfo);
        user.checkRefreshToken(jwtToken, deviceId);
        user.checkPushKey(pushKey, deviceId);
        return jwtToken;
    }

}
