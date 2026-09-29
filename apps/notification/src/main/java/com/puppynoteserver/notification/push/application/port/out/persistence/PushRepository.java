package com.puppynoteserver.notification.push.application.port.out.persistence;

import com.puppynoteserver.notification.push.domain.entity.Push;

import java.util.List;
import java.util.Optional;

public interface PushRepository {
	void deleteAllInBatch();
	Push save(Push push);
	void saveAll(List<Push> pushes);
	Optional<Push> findByUserId(Long userId);

	Optional<Push> findByDeviceId(String deviceId);

	List<Push> findAllByUserId(Long userId);

	List<Push> findAllByUserIds(List<Long> userIds);
}
