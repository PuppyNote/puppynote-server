package com.puppynoteserver.user.push.repository.impl;

import com.puppynoteserver.user.push.entity.Push;
import com.puppynoteserver.user.push.repository.PushJpaRepository;
import com.puppynoteserver.user.push.repository.PushRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PushRepositoryImpl implements PushRepository {

    private final PushJpaRepository pushJpaRepository;

    @Override
    public void deleteAllInBatch() {
        pushJpaRepository.deleteAllInBatch();
    }

    @Override
    public Push save(Push push) {
        return pushJpaRepository.save(push);
    }

    @Override
    public void saveAll(List<Push> pushes) {
        pushJpaRepository.saveAll(pushes);
    }

    @Override
    public Optional<Push> findByUserId(Long userId) {
        return pushJpaRepository.findByUserId(userId);
    }

    @Override
    public List<Push> findAllByUserId(Long userId) {
        return pushJpaRepository.findAllByUserId(userId);
    }
}
