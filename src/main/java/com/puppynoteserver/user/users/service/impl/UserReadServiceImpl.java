package com.puppynoteserver.user.users.service.impl;

import com.puppynoteserver.global.exception.PuppyNoteException;
import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.storage.enums.BucketKind;
import com.puppynoteserver.storage.service.S3StorageService;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.repository.UserRepository;
import com.puppynoteserver.user.users.service.UserReadService;
import com.puppynoteserver.user.users.service.response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserReadServiceImpl implements UserReadService {

    private static final String UNKNOWN_USER = "해당 회원은 존재하지 않습니다.";

    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final S3StorageService s3StorageService;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new PuppyNoteException(UNKNOWN_USER));
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new PuppyNoteException(UNKNOWN_USER));
    }

    @Override
    public List<User> findAllByEmailLike(String email) {
        return userRepository.findAllByEmailLike(email);
    }

    @Override
    public UserProfileResponse getMyProfile() {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        User user = findById(userId);
        String profileUrl = s3StorageService.createPresignedUrl(user.getProfileUrl(), BucketKind.USER_PROFILE);
        return UserProfileResponse.of(user, profileUrl);
    }
}
