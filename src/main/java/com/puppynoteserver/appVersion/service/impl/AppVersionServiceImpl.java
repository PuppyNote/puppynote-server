package com.puppynoteserver.appVersion.service.impl;

import com.puppynoteserver.appVersion.service.AppVersionService;
import com.puppynoteserver.appVersion.service.response.AppVersionResponse;
import org.springframework.stereotype.Service;

@Service
public class AppVersionServiceImpl implements AppVersionService {

    // 최신 버전이 나올 때마다 이 값을 갱신하고 재배포한다.
    private static final String IOS_VERSION = "1.0.2";
    private static final String AOS_VERSION = "1.0.2";
    private static final String IOS_STORE_URL = "https://apps.apple.com/kr/app/puppynote/id6760515755";
    private static final String AOS_STORE_URL = "https://play.google.com/store/apps/details?id=com.puppynote";

    @Override
    public AppVersionResponse getAppVersion() {
        return AppVersionResponse.of(IOS_VERSION, AOS_VERSION, IOS_STORE_URL, AOS_STORE_URL);
    }
}
