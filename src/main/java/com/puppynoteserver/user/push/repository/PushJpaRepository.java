package com.puppynoteserver.user.push.repository;

import com.puppynoteserver.user.push.entity.Push;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushJpaRepository extends JpaRepository<Push, Long> {
}
