package com.puppynoteserver.user.push.repository;


import java.util.List;
import java.util.Optional;

import com.puppynoteserver.user.push.entity.Push;

public interface PushRepository {
	void deleteAllInBatch();
	Push save(Push push);
	void saveAll(List<Push> pushes);
	Optional<Push> findByUserId(Long userId);
}
