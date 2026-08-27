package com.puppynoteserver.appVersion.repository;

import com.puppynoteserver.appVersion.entity.AppVersion;
import com.puppynoteserver.appVersion.entity.enums.Platform;

import java.util.Optional;

public interface AppVersionRepository {

    Optional<AppVersion> findByPlatform(Platform platform);
}
