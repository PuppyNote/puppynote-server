package com.puppynoteserver.user.users.service.impl;

import com.puppynoteserver.global.email.EmailService;
import com.puppynoteserver.global.exception.PuppyNoteException;
import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.storage.enums.BucketKind;
import com.puppynoteserver.storage.service.S3StorageService;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.entity.enums.Role;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import com.puppynoteserver.user.users.repository.UserRepository;
import com.puppynoteserver.user.users.service.UserReadService;
import com.puppynoteserver.user.users.service.UserService;
import com.puppynoteserver.user.users.service.request.EmailSendServiceRequest;
import com.puppynoteserver.user.users.service.request.SignUpServiceRequest;
import com.puppynoteserver.user.users.service.request.UserProfileUpdateServiceRequest;
import com.puppynoteserver.user.users.service.response.SignUpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserReadService userReadService;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SecurityService securityService;
    private final S3StorageService s3StorageService;

    @Override
    public SignUpResponse signUp(SignUpServiceRequest request) {
        checkExistEmail(request.getEmail());
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
        checkExistEmail(request.getEmail());
        return emailService.sendVerificationCode(request.getEmail());
    }

    @Override
    public void updateProfile(UserProfileUpdateServiceRequest request) {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        User user = userReadService.findById(userId);
        String oldProfileUrl = user.getProfileUrl();

        user.updateNickName(request.getNickName());
        user.updateProfileUrl(request.getProfileUrl());

        // 프로필 이미지가 변경된 경우 기존 이미지 S3에서 삭제
        if (oldProfileUrl != null && !Objects.equals(oldProfileUrl, request.getProfileUrl())) {
            s3StorageService.deleteObject(oldProfileUrl, BucketKind.USER_PROFILE);
        }
    }

    @Override
    public void withdraw() {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        User user = userReadService.findById(userId);
        user.withdraw();
    }

    private void checkExistEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new PuppyNoteException("이미 사용 중인 이메일입니다.");
        }
    }
}
