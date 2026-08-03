package com.puppynoteserver.user.push.service.impl;

import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.user.push.entity.Push;
import com.puppynoteserver.user.push.repository.PushRepository;
import com.puppynoteserver.user.push.service.PushReadService;
import com.puppynoteserver.user.push.service.PushWriteService;
import com.puppynoteserver.user.push.service.request.DeviceTokenRegisterServiceRequest;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.service.UserReadService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PushWriteServiceImpl implements PushWriteService {

    private final PushRepository pushRepository;
    private final PushReadService pushReadService;
    private final SecurityService securityService;
    private final UserReadService userReadService;

    /**
     * deviceId 기준으로 전역 upsert 처리.
     * 같은 디바이스가 다른 유저로 로그인하면 소유권을 현재 유저로 이전한다.
     */
    @Override
    public void upsertByDeviceId(String deviceId, User user, String pushToken) {
        pushReadService.findByDeviceId(deviceId)
                .ifPresentOrElse(
                        push -> {
                            push.updatePushToken(pushToken);
                            if (!push.getUser().getId().equals(user.getId())) {
                                push.updateUser(user);
                            }
                        },
                        () -> pushRepository.save(Push.of(deviceId, user, pushToken))
                );
    }

    /**
     * 로그인 이후 디바이스 토큰을 단독으로 등록/갱신한다.
     * 네이티브 앱, 웹뷰, 모바일 웹이 플랫폼 구분값만 달리해서 동일하게 사용한다.
     */
    @Override
    public void registerDeviceToken(DeviceTokenRegisterServiceRequest request) {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        User user = userReadService.findById(userId);

        pushReadService.findByDeviceId(request.getDeviceId())
                .ifPresentOrElse(
                        push -> {
                            push.updateDeviceToken(request.getPushToken(), request.getPlatform());
                            // 같은 디바이스가 다른 유저로 재로그인한 경우 소유권을 현재 유저로 이전한다.
                            if (!push.getUser().getId().equals(user.getId())) {
                                push.updateUser(user);
                            }
                        },
                        () -> pushRepository.save(request.toEntity(user))
                );
    }
}
