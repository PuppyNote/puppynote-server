package com.puppynoteserver.user.user.application;

import com.puppynoteserver.global.exception.PuppyNoteException;
import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.user.user.application.port.in.UserFinder;
import com.puppynoteserver.user.user.application.port.in.UserRegister;
import com.puppynoteserver.user.user.application.port.in.UserRemover;
import com.puppynoteserver.user.user.application.port.in.UserUpdater;
import com.puppynoteserver.user.user.application.port.in.request.EmailSendServiceRequest;
import com.puppynoteserver.user.user.application.port.in.request.SignUpServiceRequest;
import com.puppynoteserver.user.user.application.port.in.request.UserProfileUpdateServiceRequest;
import com.puppynoteserver.user.user.application.port.in.response.SignUpResponse;
import com.puppynoteserver.user.user.application.port.in.response.UserProfileResponse;
import com.puppynoteserver.user.user.application.port.out.persistence.UserRepository;
import com.puppynoteserver.user.user.domain.entity.User;
import com.puppynoteserver.user.user.domain.enums.Role;
import com.puppynoteserver.user.user.domain.enums.SnsType;
import com.puppynoteserver.user.user.domain.error.UserErrorMessage;
import com.puppynoteserver.storage.application.port.out.FileStorage;
import com.puppynoteserver.storage.enums.BucketKind;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserFinder, UserRegister, UserUpdater, UserRemover {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SecurityService securityService;
    private final FileStorage fileStorage;


    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new PuppyNoteException(UserErrorMessage.UNKNOWN_USER.getMessage()));
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new PuppyNoteException(UserErrorMessage.UNKNOWN_USER.getMessage()));
    }

    @Override
    public List<User> findAllByEmailLike(String email) {
        return userRepository.findAllByEmailLike(email);
    }

    @Override
    public UserProfileResponse getMyProfile() {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        User user = findById(userId);
        String profileUrl = fileStorage.getCloudFrontUrl(user.getProfileUrl(), BucketKind.USER_PROFILE);
        return UserProfileResponse.of(user, profileUrl);
    }

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
        User user = findById(userId);
        String oldProfileUrl = user.getProfileUrl();

        user.updateNickName(request.getNickName());
        user.updateProfileUrl(request.getProfileUrl());

        // 프로필 이미지가 변경된 경우 기존 이미지 S3에서 삭제
        if (oldProfileUrl != null && !Objects.equals(oldProfileUrl, request.getProfileUrl())) {
            fileStorage.deleteObject(oldProfileUrl, BucketKind.USER_PROFILE);
        }
    }

    @Override
    public void withdraw() {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        User user = findById(userId);
        user.withdraw();
    }

    private void checkExistEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new PuppyNoteException(UserErrorMessage.EXIST_EMAIL.getMessage());
        }
    }
}
