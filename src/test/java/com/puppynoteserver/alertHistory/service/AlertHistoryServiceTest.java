package com.puppynoteserver.alertHistory.service;

import com.puppynoteserver.IntegrationTestSupport;
import com.puppynoteserver.alertHistory.entity.AlertDestinationType;
import com.puppynoteserver.alertHistory.entity.AlertHistory;
import com.puppynoteserver.alertHistory.entity.AlertHistoryStatus;
import com.puppynoteserver.alertHistory.repository.AlertHistoryRepository;
import com.puppynoteserver.alertHistory.service.alertHistory.AlertHistoryReadService;
import com.puppynoteserver.alertHistory.service.alertHistory.AlertHistoryService;
import com.puppynoteserver.alertHistory.service.alertHistory.request.AlertHistoryServiceRequest;
import com.puppynoteserver.alertHistory.service.alertHistory.response.AlertHistoryResponse;
import com.puppynoteserver.alertHistory.service.alertHistory.response.AlertHistoryStatusResponse;
import com.puppynoteserver.global.page.request.PageInfoServiceRequest;
import com.puppynoteserver.global.page.response.PageCustom;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

class AlertHistoryServiceTest extends IntegrationTestSupport {

    @Autowired
    private AlertHistoryService alertHistoryService;

    @Autowired
    private AlertHistoryReadService alertHistoryReadService;

    @Autowired
    private AlertHistoryRepository alertHistoryRepository;

    @AfterEach
    @Override
    public void tearDown() {
        alertHistoryRepository.deleteAllInBatch();
        super.tearDown();
    }

    @DisplayName("알림 내역을 UNCHECKED 상태로 생성한다.")
    @Test
    void createAlertHistory() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        AlertHistoryServiceRequest request = AlertHistoryServiceRequest.builder()
                .user(user)
                .alertDescription("친구 요청이 도착했습니다.")
                .alertDestinationType(AlertDestinationType.FRIEND_CODE)
                .alertDestinationInfo("ABC123")
                .build();

        // when
        AlertHistory result = alertHistoryService.createAlertHistory(request);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getAlertDescription()).isEqualTo("친구 요청이 도착했습니다.");
        assertThat(result.getAlertHistoryStatus()).isEqualTo(AlertHistoryStatus.UNCHECKED);
        assertThat(result.getAlertDestinationType()).isEqualTo(AlertDestinationType.FRIEND_CODE);
        assertThat(result.getAlertDestinationInfo()).isEqualTo("ABC123");
    }

    @DisplayName("알림 내역 상태를 CHECKED로 변경한다.")
    @Test
    void updateAlertHistoryStatus() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        AlertHistory alertHistory = alertHistoryRepository.save(
                AlertHistory.builder()
                        .user(user)
                        .alertDescription("테스트 알림")
                        .alertHistoryStatus(AlertHistoryStatus.UNCHECKED)
                        .alertDestinationType(AlertDestinationType.FRIEND)
                        .alertDestinationInfo("friendInfo")
                        .build()
        );

        // when
        AlertHistoryStatusResponse result = alertHistoryService.updateAlertHistoryStatus(alertHistory.getId());

        // then
        assertThat(result.getAlertHistoryStatus()).isEqualTo(AlertHistoryStatus.CHECKED);
    }

    @DisplayName("존재하지 않는 알림 내역 ID로 상태 변경 시 예외가 발생한다.")
    @Test
    void updateAlertHistoryStatus_notFound() {
        // given
        Long nonExistentId = 999L;

        // when & then
        assertThatThrownBy(() -> alertHistoryService.updateAlertHistoryStatus(nonExistentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 알림 내역은 없습니다");
    }

    @DisplayName("현재 로그인한 사용자의 알림 내역 목록을 페이지네이션으로 조회한다.")
    @Test
    void getAlertHistory() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        alertHistoryRepository.save(AlertHistory.builder()
                .user(user)
                .alertDescription("첫 번째 알림")
                .alertHistoryStatus(AlertHistoryStatus.UNCHECKED)
                .alertDestinationType(AlertDestinationType.FRIEND_CODE)
                .alertDestinationInfo("CODE1")
                .build());
        alertHistoryRepository.save(AlertHistory.builder()
                .user(user)
                .alertDescription("두 번째 알림")
                .alertHistoryStatus(AlertHistoryStatus.CHECKED)
                .alertDestinationType(AlertDestinationType.FRIEND)
                .alertDestinationInfo("friendInfo")
                .build());

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        PageInfoServiceRequest request = PageInfoServiceRequest.builder()
                .page(1)
                .size(12)
                .build();

        // when
        PageCustom<AlertHistoryResponse> result = alertHistoryReadService.getAlertHistory(request);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getPageInfo().getCurrentPage()).isEqualTo(1);
        assertThat(result.getPageInfo().getTotalElement()).isEqualTo(2);
    }

    @DisplayName("미확인 알림이 있으면 hasUncheckedAlerts가 true를 반환한다.")
    @Test
    void hasUncheckedAlerts_returnsTrue() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        alertHistoryRepository.save(AlertHistory.builder()
                .user(user)
                .alertDescription("미확인 알림")
                .alertHistoryStatus(AlertHistoryStatus.UNCHECKED)
                .alertDestinationType(AlertDestinationType.FRIEND)
                .alertDestinationInfo("friendInfo")
                .build());

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        // when
        boolean result = alertHistoryReadService.hasUncheckedAlerts();

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("미확인 알림이 없으면 hasUncheckedAlerts가 false를 반환한다.")
    @Test
    void hasUncheckedAlerts_returnsFalse() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        alertHistoryRepository.save(AlertHistory.builder()
                .user(user)
                .alertDescription("확인된 알림")
                .alertHistoryStatus(AlertHistoryStatus.CHECKED)
                .alertDestinationType(AlertDestinationType.FRIEND)
                .alertDestinationInfo("friendInfo")
                .build());

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        // when
        boolean result = alertHistoryReadService.hasUncheckedAlerts();

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("해당 friendCode가 존재하면 hasFriendCode가 true를 반환한다.")
    @Test
    void hasFriendCode_returnsTrue() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        String friendCode = "ABC123";
        alertHistoryRepository.save(AlertHistory.builder()
                .user(user)
                .alertDescription("친구 코드 알림")
                .alertHistoryStatus(AlertHistoryStatus.UNCHECKED)
                .alertDestinationType(AlertDestinationType.FRIEND_CODE)
                .alertDestinationInfo(friendCode)
                .build());

        // when
        boolean result = alertHistoryReadService.hasFriendCode(user.getId(), friendCode);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("해당 friendCode가 없으면 hasFriendCode가 false를 반환한다.")
    @Test
    void hasFriendCode_returnsFalse() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));

        // when
        boolean result = alertHistoryReadService.hasFriendCode(user.getId(), "NONEXISTENT");

        // then
        assertThat(result).isFalse();
    }
}
