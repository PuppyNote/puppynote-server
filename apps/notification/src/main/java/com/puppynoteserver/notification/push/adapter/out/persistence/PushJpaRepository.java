package com.puppynoteserver.notification.push.adapter.out.persistence;

import com.puppynoteserver.notification.push.domain.entity.Push;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PushJpaRepository extends JpaRepository<Push, Long> {

    Optional<Push> findByUserId(Long userId);

    Optional<Push> findByDeviceId(String deviceId);

    List<Push> findAllByUserId(Long userId);

    List<Push> findAllByUserIdIn(List<Long> userIds);
}
