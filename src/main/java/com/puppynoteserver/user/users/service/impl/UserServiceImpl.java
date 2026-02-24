package com.puppynoteserver.user.users.service.impl;

import com.puppynoteserver.global.email.EmailService;
import com.puppynoteserver.global.exception.PuppyNoteException;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.entity.enums.Role;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import com.puppynoteserver.user.users.repository.UserRepository;
import com.puppynoteserver.user.users.service.UserService;
import com.puppynoteserver.user.users.service.request.EmailSendServiceRequest;
import com.puppynoteserver.user.users.service.request.EmailVerifyServiceRequest;
import com.puppynoteserver.user.users.service.request.SignUpServiceRequest;
import com.puppynoteserver.user.users.service.response.SignUpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public SignUpResponse signUp(SignUpServiceRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new PuppyNoteException("이미 사용 중인 이메일입니다.");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickName(request.getNickName())
                .snsType(SnsType.NORMAL)
                .role(Role.USER)
                .useYn("Y")
                .build();
        return SignUpResponse.of(userRepository.save(user));
    }

    @Override
    public String sendVerificationEmail(EmailSendServiceRequest request) {
        return emailService.sendVerificationCode(request.getEmail());
    }
}
