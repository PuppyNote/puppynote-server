package com.puppynoteserver.pet.walk.repository;

import com.puppynoteserver.pet.walk.entity.Walk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalkJpaRepository extends JpaRepository<Walk, Long> {
}
