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
import com.puppynoteserver.user.users.service.request.LoginServiceRequest;
import com.puppynoteserver.user.users.service.request.OAuthLoginServiceRequest;
import com.puppynoteserver.user.users.service.response.LoginResponse;
import com.puppynoteserver.user.users.service.response.OAuthLoginResponse;
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

    public LoginServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenGenerator jwtTokenGenerator,
                            List<OAuthApiClient> clients, UserRepository userRepository, UserReadService userReadService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.clients = clients.stream().collect(
                Collectors.toUnmodifiableMap(OAuthApiClient::oAuthSnsType, Function.identity())
        );
        this.userReadService = userReadService;
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
                                .build()
                ));

        user.checkSnsType(snsType);              //SNS가입여부확인

        JwtToken jwtToken = setJwtTokenPushKey(user, oAuthLoginServiceRequest.getDeviceId(),
                oAuthLoginServiceRequest.getPushKey());

        return OAuthLoginResponse.of(user, jwtToken);
    }

    private JwtToken setJwtTokenPushKey(User user, String deviceId, String pushKey) throws JsonProcessingException {
        LoginUserInfo userInfo = LoginUserInfo.of(user.getId());
        JwtToken jwtToken = jwtTokenGenerator.generate(userInfo);
        user.checkRefreshToken(jwtToken, deviceId);
        user.checkPushKey(pushKey, deviceId);
        return jwtToken;
    }

}
