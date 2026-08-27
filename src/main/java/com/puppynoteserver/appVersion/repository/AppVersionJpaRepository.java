package com.puppynoteserver.appVersion.repository;

import com.puppynoteserver.appVersion.entity.AppVersion;
import com.puppynoteserver.appVersion.entity.enums.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppVersionJpaRepository extends JpaRepository<AppVersion, Long> {

    Optional<AppVersion> findByPlatform(Platform platform);
}
