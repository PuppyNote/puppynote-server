package com.puppynoteserver.user.user.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.puppynoteserver.global.exception.PuppyNoteException;
import com.puppynoteserver.jwt.JwtTokenGenerator;
import com.puppynoteserver.jwt.dto.JwtToken;
import com.puppynoteserver.jwt.dto.LoginUserInfo;
import com.puppynoteserver.user.session.application.port.in.SessionManager;
import com.puppynoteserver.user.user.application.port.in.LoginManager;
import com.puppynoteserver.user.user.application.port.in.request.EmailSendServiceRequest;
import com.puppynoteserver.user.user.application.port.in.request.LoginServiceRequest;
import com.puppynoteserver.user.user.application.port.in.request.OAuthLoginServiceRequest;
import com.puppynoteserver.user.user.application.port.in.request.PasswordResetServiceRequest;
import com.puppynoteserver.user.user.application.port.in.response.LoginResponse;
import com.puppynoteserver.user.user.application.port.in.response.OAuthLoginResponse;
import com.puppynoteserver.user.user.application.port.out.OAuthApiClient;
import com.puppynoteserver.user.user.application.port.out.persistence.UserRepository;
import com.puppynoteserver.user.user.domain.entity.User;
import com.puppynoteserver.user.user.domain.enums.Role;
import com.puppynoteserver.user.user.domain.enums.SnsType;
import com.puppynoteserver.user.user.domain.error.UserErrorMessage;
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
public class LoginService implements LoginManager {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final Map<SnsType, OAuthApiClient> clients;
    private final EmailService emailService;

    private final UserRepository userRepository;
    private final SessionManager sessionManager;

    public LoginService(BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenGenerator jwtTokenGenerator,
                        List<OAuthApiClient> clients, UserRepository userRepository,
                        SessionManager sessionManager, EmailService emailService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.clients = clients.stream().collect(
                Collectors.toUnmodifiableMap(OAuthApiClient::oAuthSnsType, Function.identity())
        );
        this.sessionManager = sessionManager;
        this.emailService = emailService;
    }

    @Override
    public LoginResponse normalLogin(LoginServiceRequest loginServiceRequest) throws JsonProcessingException {
        User user = findByEmail(loginServiceRequest.getEmail());
        user.checkSnsType(SnsType.NORMAL);                                     //SNS가입여부확인

        if (!bCryptPasswordEncoder.matches(loginServiceRequest.getPassword(), user.getPassword())) {
            throw new PuppyNoteException("아이디 또는 패스워드가 일치하지 않습니다.");
        } //3. 비밀번호 체크

        JwtToken jwtToken = setJwtTokenPushKey(user, loginServiceRequest.getDeviceId());

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
                                .nickName(email.split("@")[0])
                                .snsType(snsType)
                                .role(Role.USER)
                                .useYn("Y")
                                .build()
                ));

        user.checkSnsType(snsType);              //SNS가입여부확인

        JwtToken jwtToken = setJwtTokenPushKey(user, oAuthLoginServiceRequest.getDeviceId());

        return OAuthLoginResponse.of(user, jwtToken);
    }

    @Override
    public String sendPasswordResetEmail(EmailSendServiceRequest request) {
        User user = findByEmail(request.getEmail());
        checkSnsType(user);
        return emailService.sendVerificationCode(request.getEmail());
    }

    @Override
    public void resetPassword(PasswordResetServiceRequest request) {
        User user = findByEmail(request.getEmail());
        checkSnsType(user);
        user.updatePassword(bCryptPasswordEncoder.encode(request.getNewPassword()));
    }

    private JwtToken setJwtTokenPushKey(User user, String deviceId) throws JsonProcessingException {
        LoginUserInfo userInfo = LoginUserInfo.of(user.getId());
        JwtToken jwtToken = jwtTokenGenerator.generate(userInfo);
        sessionManager.upsertByDeviceId(user, jwtToken, deviceId);
        return jwtToken;
    }

    private void checkSnsType(User user) {
        if (user.getSnsType() != SnsType.NORMAL) {
            throw new PuppyNoteException(user.getSnsType().getText() + "로 가입된 계정입니다.");
        }
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new PuppyNoteException(UserErrorMessage.UNKNOWN_EMAIL.getMessage()));
    }

}
