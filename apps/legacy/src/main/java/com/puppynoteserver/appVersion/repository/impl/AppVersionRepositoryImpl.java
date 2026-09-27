package com.puppynoteserver.appVersion.repository.impl;

import com.puppynoteserver.appVersion.entity.AppVersion;
import com.puppynoteserver.appVersion.entity.enums.Platform;
import com.puppynoteserver.appVersion.repository.AppVersionJpaRepository;
import com.puppynoteserver.appVersion.repository.AppVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AppVersionRepositoryImpl implements AppVersionRepository {

    private final AppVersionJpaRepository appVersionJpaRepository;

    @Override
    public Optional<AppVersion> findByPlatform(Platform platform) {
        return appVersionJpaRepository.findByPlatform(platform);
    }
}
