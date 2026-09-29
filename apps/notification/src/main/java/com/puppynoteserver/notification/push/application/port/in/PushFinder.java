package com.puppynoteserver.notification.push.application.port.in;

import com.puppynoteserver.notification.push.domain.entity.Push;

import java.util.List;
import java.util.Optional;

public interface PushFinder {

    Optional<Push> findByUserId(Long userId);

    Optional<Push> findByDeviceId(String deviceId);

    List<Push> findAllByUserId(Long userId);
}
