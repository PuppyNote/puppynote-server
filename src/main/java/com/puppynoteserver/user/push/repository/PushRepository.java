package com.puppynoteserver.user.push.repository;


import java.util.List;

import com.puppynoteserver.user.push.entity.Push;

public interface PushRepository {
	void deleteAllInBatch();
	Push save(Push push);
	void saveAll(List<Push> pushes);
}
