package com.puppynoteserver.appVersion.service.response;

import lombok.Getter;

@Getter
public class AppVersionResponse {

    private final String iosVersion;
    private final String aosVersion;
    private final String iosStoreUrl;
    private final String aosStoreUrl;

    private AppVersionResponse(String iosVersion, String aosVersion, String iosStoreUrl, String aosStoreUrl) {
        this.iosVersion = iosVersion;
        this.aosVersion = aosVersion;
        this.iosStoreUrl = iosStoreUrl;
        this.aosStoreUrl = aosStoreUrl;
    }

    public static AppVersionResponse of(String iosVersion, String aosVersion, String iosStoreUrl, String aosStoreUrl) {
        return new AppVersionResponse(iosVersion, aosVersion, iosStoreUrl, aosStoreUrl);
    }
}
