package com.puppynoteserver.user.push.service;

import com.puppynoteserver.user.push.entity.Push;

import java.util.List;
import java.util.Optional;

public interface PushReadService {

    Optional<Push> findByUserId(Long userId);

    List<Push> findAllByUserId(Long userId);
}
