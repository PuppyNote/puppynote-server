package com.puppynoteserver.appVersion.service;

import com.puppynoteserver.IntegrationTestSupport;
import com.puppynoteserver.appVersion.entity.AppVersion;
import com.puppynoteserver.appVersion.entity.enums.Platform;
import com.puppynoteserver.appVersion.repository.AppVersionJpaRepository;
import com.puppynoteserver.appVersion.service.response.AppVersionResponse;
import com.puppynoteserver.global.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
class AppVersionServiceImplTest extends IntegrationTestSupport {

    @Autowired
    private AppVersionService appVersionService;

    @Autowired
    private AppVersionJpaRepository appVersionJpaRepository;

    @DisplayName("iOS/AOS 버전 정보가 모두 등록되어 있으면 플랫폼별 최신 버전과 스토어 URL을 반환한다.")
    @Test
    void iOS_AOS_버전_정보가_모두_있으면_getAppVersion_플랫폼별_정보를_반환한다() {
        // given
        appVersionJpaRepository.save(AppVersion.builder()
                .platform(Platform.IOS)
                .latestVersion("1.0.2")
                .minSupportedVersion("1.0.2")
                .forceUpdate(false)
                .storeUrl("https://apps.apple.com/kr/app/puppynote/id6760515755")
                .build());
        appVersionJpaRepository.save(AppVersion.builder()
                .platform(Platform.AOS)
                .latestVersion("1.0.2")
                .minSupportedVersion("1.0.2")
                .forceUpdate(false)
                .storeUrl("https://play.google.com/store/apps/details?id=com.puppynote")
                .build());

        // when
        AppVersionResponse response = appVersionService.getAppVersion();

        // then
        assertAll(
                () -> assertThat(response.getIosVersion()).isEqualTo("1.0.2"),
                () -> assertThat(response.getAosVersion()).isEqualTo("1.0.2"),
                () -> assertThat(response.getIosStoreUrl()).isEqualTo("https://apps.apple.com/kr/app/puppynote/id6760515755"),
                () -> assertThat(response.getAosStoreUrl()).isEqualTo("https://play.google.com/store/apps/details?id=com.puppynote")
        );
    }

    @DisplayName("특정 플랫폼의 버전 정보가 없으면 NotFoundException이 발생한다.")
    @Test
    void 플랫폼_버전_정보가_없으면_getAppVersion_NotFoundException이_발생한다() {
        // given
        appVersionJpaRepository.save(AppVersion.builder()
                .platform(Platform.IOS)
                .latestVersion("1.0.2")
                .minSupportedVersion("1.0.2")
                .forceUpdate(false)
                .storeUrl("https://apps.apple.com/kr/app/puppynote/id6760515755")
                .build());

        // when // then
        assertThatThrownBy(() -> appVersionService.getAppVersion())
                .isInstanceOf(NotFoundException.class);
    }
}
