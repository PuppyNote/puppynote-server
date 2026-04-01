package com.puppynoteserver.user.push.service.impl;

import com.puppynoteserver.user.push.entity.Push;
import com.puppynoteserver.user.push.repository.PushRepository;
import com.puppynoteserver.user.push.service.PushReadService;
import com.puppynoteserver.user.push.service.PushWriteService;
import com.puppynoteserver.user.users.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PushWriteServiceImpl implements PushWriteService {

    private final PushRepository pushRepository;
    private final PushReadService pushReadService;

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
}
