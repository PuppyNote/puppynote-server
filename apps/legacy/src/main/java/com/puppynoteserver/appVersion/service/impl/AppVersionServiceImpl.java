package com.puppynoteserver.appVersion.service.impl;

import com.puppynoteserver.appVersion.entity.AppVersion;
import com.puppynoteserver.appVersion.entity.enums.Platform;
import com.puppynoteserver.appVersion.repository.AppVersionRepository;
import com.puppynoteserver.appVersion.service.AppVersionService;
import com.puppynoteserver.appVersion.service.response.AppVersionResponse;
import com.puppynoteserver.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppVersionServiceImpl implements AppVersionService {

    private final AppVersionRepository appVersionRepository;

    @Override
    public AppVersionResponse getAppVersion() {
        AppVersion ios = getByPlatform(Platform.IOS);
        AppVersion aos = getByPlatform(Platform.AOS);
        return AppVersionResponse.of(ios.getLatestVersion(), aos.getLatestVersion(), ios.getStoreUrl(), aos.getStoreUrl());
    }

    private AppVersion getByPlatform(Platform platform) {
        return appVersionRepository.findByPlatform(platform)
                .orElseThrow(() -> new NotFoundException("등록된 앱 버전 정보가 없습니다. platform=" + platform));
    }
}
