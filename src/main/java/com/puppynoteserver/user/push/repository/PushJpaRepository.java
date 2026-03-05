package com.puppynoteserver.user.push.repository;

import com.puppynoteserver.user.push.entity.Push;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PushJpaRepository extends JpaRepository<Push, Long> {

    Optional<Push> findByUserId(Long userId);

    List<Push> findAllByUserId(Long userId);
}
