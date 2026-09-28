package com.puppynoteserver.appVersion.entity;

import com.puppynoteserver.appVersion.entity.enums.Platform;
import com.puppynoteserver.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "app_versions")
public class AppVersion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 10)
    private Platform platform;

    @Column(nullable = false, length = 20)
    private String latestVersion;

    @Column(nullable = false, length = 20)
    private String minSupportedVersion;

    private Integer buildNumber;

    @Column(nullable = false)
    private boolean forceUpdate;

    @Column(nullable = false, length = 255)
    private String storeUrl;

    @Builder
    private AppVersion(Platform platform, String latestVersion, String minSupportedVersion,
                        Integer buildNumber, boolean forceUpdate, String storeUrl) {
        this.platform = platform;
        this.latestVersion = latestVersion;
        this.minSupportedVersion = minSupportedVersion;
        this.buildNumber = buildNumber;
        this.forceUpdate = forceUpdate;
        this.storeUrl = storeUrl;
    }
}
