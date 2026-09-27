package com.puppynoteserver.alertSetting.service;

import com.puppynoteserver.IntegrationTestSupport;
import com.puppynoteserver.alertSetting.entity.AlertSetting;
import com.puppynoteserver.alertSetting.entity.enums.AlertType;
import com.puppynoteserver.alertSetting.repository.AlertSettingRepository;
import com.puppynoteserver.alertSetting.service.request.AlertSettingUpdateServiceRequest;
import com.puppynoteserver.alertSetting.service.response.AlertSettingResponse;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class AlertSettingServiceTest extends IntegrationTestSupport {

    @Autowired
    private AlertSettingService alertSettingService;

    @Autowired
    private AlertSettingReadService alertSettingReadService;

    @Autowired
    private AlertSettingRepository alertSettingRepository;

    @AfterEach
    @Override
    public void tearDown() {
        alertSettingRepository.deleteAllInBatch();
        super.tearDown();
    }

    @DisplayName("기존 알림 설정을 수정한다.")
    @Test
    void 알림_설정이_있을_때_수정하면_변경된_값이_반환된다() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        alertSettingRepository.save(AlertSetting.createDefault(user));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        AlertSettingUpdateServiceRequest request = AlertSettingUpdateServiceRequest.builder()
                .all(AlertType.ON)
                .walk(AlertType.OFF)
                .friend(AlertType.ON)
                .build();

        // when
        AlertSettingResponse result = alertSettingService.updateAlertSetting(request);

        // then
        assertThat(result)
                .extracting("all", "walk", "friend")
                .containsExactly(AlertType.ON, AlertType.OFF, AlertType.ON);
    }

    @DisplayName("알림 설정이 없을 때 수정하면 기본값이 생성된 후 수정된다.")
    @Test
    void 알림_설정이_없을_때_수정하면_기본값_생성_후_변경된다() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        AlertSettingUpdateServiceRequest request = AlertSettingUpdateServiceRequest.builder()
                .all(AlertType.OFF)
                .walk(AlertType.OFF)
                .friend(AlertType.OFF)
                .build();

        // when
        AlertSettingResponse result = alertSettingService.updateAlertSetting(request);

        // then
        assertThat(result)
                .extracting("all", "walk", "friend")
                .containsExactly(AlertType.OFF, AlertType.OFF, AlertType.OFF);
    }

    @DisplayName("알림 설정을 조회한다.")
    @Test
    void 알림_설정이_있을_때_조회하면_설정값을_반환한다() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        alertSettingRepository.save(AlertSetting.createDefault(user));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        // when
        AlertSettingResponse result = alertSettingReadService.getAlertSetting();

        // then
        assertThat(result)
                .extracting("all", "walk", "friend")
                .containsExactly(AlertType.ON, AlertType.ON, AlertType.ON);
    }

    @DisplayName("알림 설정이 없으면 기본값(모두 ON)으로 생성하여 반환한다.")
    @Test
    void 알림_설정이_없을_때_조회하면_기본값으로_생성하여_반환한다() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        // when
        AlertSettingResponse result = alertSettingReadService.getAlertSetting();

        // then
        assertThat(result)
                .extracting("all", "walk", "friend")
                .containsExactly(AlertType.ON, AlertType.ON, AlertType.ON);
    }
}
