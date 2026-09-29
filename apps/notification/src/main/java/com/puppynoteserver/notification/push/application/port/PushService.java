package com.puppynoteserver.notification.push.application.port;

import com.puppynoteserver.notification.push.application.port.in.PushFinder;
import com.puppynoteserver.notification.push.application.port.in.PushUpdater;
import com.puppynoteserver.notification.push.application.port.out.persistence.PushRepository;
import com.puppynoteserver.notification.push.domain.entity.Push;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PushService implements PushUpdater, PushFinder {

    private final PushRepository pushRepository;

    /**
     * deviceId 기준으로 전역 upsert 처리.
     * 같은 디바이스가 다른 유저로 로그인하면 소유권을 현재 유저로 이전한다.
     */
    @Override
    public void upsertByDeviceId(String deviceId, Long userId, String pushToken) {
        findByDeviceId(deviceId)
                .ifPresentOrElse(
                        push -> {
                            push.updatePushToken(pushToken);
                            if (!push.getUserId().equals(userId)) {
                                push.updateUser(userId);
                            }
                        },
                        () -> pushRepository.save(Push.of(deviceId, userId, pushToken))
                );
    }


    @Override
    public Optional<Push> findByUserId(Long userId) {
        return pushRepository.findByUserId(userId);
    }

    @Override
    public Optional<Push> findByDeviceId(String deviceId) {
        return pushRepository.findByDeviceId(deviceId);
    }

    @Override
    public List<Push> findAllByUserId(Long userId) {
        return pushRepository.findAllByUserId(userId);
    }
}
