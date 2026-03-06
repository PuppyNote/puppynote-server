package com.puppynoteserver.user.push.service.impl;

import com.puppynoteserver.user.push.entity.Push;
import com.puppynoteserver.user.push.repository.PushRepository;
import com.puppynoteserver.user.push.service.PushReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PushReadServiceImpl implements PushReadService {

    private final PushRepository pushRepository;

    @Override
    public Optional<Push> findByUserId(Long userId) {
        return pushRepository.findByUserId(userId);
    }
}
