package com.puppynoteserver.user.push.service;

import com.puppynoteserver.IntegrationTestSupport;
import com.puppynoteserver.user.push.entity.Push;
import com.puppynoteserver.user.push.entity.enums.DevicePlatform;
import com.puppynoteserver.user.push.service.request.DeviceTokenRegisterServiceRequest;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@Transactional
public class PushWriteServiceTest extends IntegrationTestSupport {

    @Autowired
    private PushWriteService pushWriteService;

    @Autowired
    private PushReadService pushReadService;

    @DisplayName("deviceId가 없을 때 새로운 Push를 저장한다.")
    @Test
    void deviceId가_없을_때_upsertByDeviceId_새로운_Push를_저장한다() {
        // given
        User user = createUser("test@test.com", "1234", SnsType.NORMAL);
        User savedUser = userRepository.save(user);

        String deviceId = "device-001";
        String pushToken = "push-token-001";

        // when
        pushWriteService.upsertByDeviceId(deviceId, savedUser, pushToken);

        // then
        Optional<Push> result = pushReadService.findByDeviceId(deviceId);
        assertThat(result).isPresent();
        assertAll(
                () -> assertThat(result.get().getDeviceId()).isEqualTo(deviceId),
                () -> assertThat(result.get().getPushToken()).isEqualTo(pushToken),
                () -> assertThat(result.get().getUser().getId()).isEqualTo(savedUser.getId())
        );
    }

    @DisplayName("동일한 유저가 같은 deviceId로 upsert하면 pushToken만 갱신한다.")
    @Test
    void 동일_유저_동일_deviceId로_upsertByDeviceId_pushToken만_갱신한다() {
        // given
        User user = createUser("test@test.com", "1234", SnsType.NORMAL);
        User savedUser = userRepository.save(user);

        String deviceId = "device-001";
        Push push = Push.of(deviceId, savedUser, "old-push-token");
        pushRepository.save(push);

        String newPushToken = "new-push-token";

        // when
        pushWriteService.upsertByDeviceId(deviceId, savedUser, newPushToken);

        // then
        Optional<Push> result = pushReadService.findByDeviceId(deviceId);
        assertThat(result).isPresent();
        assertAll(
                () -> assertThat(result.get().getPushToken()).isEqualTo(newPushToken),
                () -> assertThat(result.get().getUser().getId()).isEqualTo(savedUser.getId())
        );
    }

    @DisplayName("다른 유저가 같은 deviceId로 upsert하면 유저 소유권이 현재 유저로 이전된다.")
    @Test
    void 다른_유저가_동일_deviceId로_upsertByDeviceId_유저_소유권이_이전된다() {
        // given
        User originalUser = createUser("original@test.com", "1234", SnsType.NORMAL);
        User savedOriginalUser = userRepository.save(originalUser);

        User newUser = createUser("new@test.com", "1234", SnsType.NORMAL);
        User savedNewUser = userRepository.save(newUser);

        String deviceId = "device-001";
        Push push = Push.of(deviceId, savedOriginalUser, "old-push-token");
        pushRepository.save(push);

        String newPushToken = "new-push-token";

        // when
        pushWriteService.upsertByDeviceId(deviceId, savedNewUser, newPushToken);

        // then
        Optional<Push> result = pushReadService.findByDeviceId(deviceId);
        assertThat(result).isPresent();
        assertAll(
                () -> assertThat(result.get().getPushToken()).isEqualTo(newPushToken),
                () -> assertThat(result.get().getUser().getId()).isEqualTo(savedNewUser.getId())
        );
    }

    @DisplayName("등록된 디바이스가 없으면 플랫폼과 함께 새로운 Push를 저장한다.")
    @Test
    void 등록된_디바이스가_없으면_registerDeviceToken_플랫폼과_함께_저장한다() {
        // given
        User savedUser = userRepository.save(createUser("test@test.com", "1234", SnsType.NORMAL));
        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(savedUser.getId()));

        DeviceTokenRegisterServiceRequest request = DeviceTokenRegisterServiceRequest.builder()
                .deviceId("device-001")
                .pushToken("push-token-001")
                .platform(DevicePlatform.WEB)
                .build();

        // when
        pushWriteService.registerDeviceToken(request);

        // then
        Optional<Push> result = pushReadService.findByDeviceId("device-001");
        assertThat(result).isPresent();
        assertAll(
                () -> assertThat(result.get().getDeviceId()).isEqualTo("device-001"),
                () -> assertThat(result.get().getPushToken()).isEqualTo("push-token-001"),
                () -> assertThat(result.get().getPlatform()).isEqualTo(DevicePlatform.WEB),
                () -> assertThat(result.get().getUser().getId()).isEqualTo(savedUser.getId())
        );
    }

    @DisplayName("이미 등록된 디바이스면 토큰과 플랫폼을 갱신한다.")
    @Test
    void 이미_등록된_디바이스면_registerDeviceToken_토큰과_플랫폼을_갱신한다() {
        // given
        User savedUser = userRepository.save(createUser("test@test.com", "1234", SnsType.NORMAL));
        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(savedUser.getId()));

        pushRepository.save(Push.of("device-001", savedUser, "old-push-token", DevicePlatform.IOS));

        DeviceTokenRegisterServiceRequest request = DeviceTokenRegisterServiceRequest.builder()
                .deviceId("device-001")
                .pushToken("new-push-token")
                .platform(DevicePlatform.WEB)
                .build();

        // when
        pushWriteService.registerDeviceToken(request);

        // then
        Optional<Push> result = pushReadService.findByDeviceId("device-001");
        assertThat(result).isPresent();
        assertAll(
                () -> assertThat(result.get().getPushToken()).isEqualTo("new-push-token"),
                () -> assertThat(result.get().getPlatform()).isEqualTo(DevicePlatform.WEB),
                () -> assertThat(result.get().getUser().getId()).isEqualTo(savedUser.getId())
        );
    }

    @DisplayName("다른 유저가 쓰던 디바이스면 소유권이 현재 로그인 유저로 이전된다.")
    @Test
    void 다른_유저가_쓰던_디바이스면_registerDeviceToken_소유권이_이전된다() {
        // given
        User savedOriginalUser = userRepository.save(createUser("original@test.com", "1234", SnsType.NORMAL));
        User savedNewUser = userRepository.save(createUser("new@test.com", "1234", SnsType.NORMAL));
        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(savedNewUser.getId()));

        pushRepository.save(Push.of("device-001", savedOriginalUser, "old-push-token", DevicePlatform.ANDROID));

        DeviceTokenRegisterServiceRequest request = DeviceTokenRegisterServiceRequest.builder()
                .deviceId("device-001")
                .pushToken("new-push-token")
                .platform(DevicePlatform.ANDROID)
                .build();

        // when
        pushWriteService.registerDeviceToken(request);

        // then
        Optional<Push> result = pushReadService.findByDeviceId("device-001");
        assertThat(result).isPresent();
        assertAll(
                () -> assertThat(result.get().getPushToken()).isEqualTo("new-push-token"),
                () -> assertThat(result.get().getUser().getId()).isEqualTo(savedNewUser.getId())
        );
    }

    @DisplayName("로그인 경로로 등록된 디바이스는 플랫폼 값이 비어있어도 갱신된다.")
    @Test
    void 로그인_경로로_등록된_디바이스는_registerDeviceToken_플랫폼이_채워진다() {
        // given
        User savedUser = userRepository.save(createUser("test@test.com", "1234", SnsType.NORMAL));
        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(savedUser.getId()));

        // 기존 로그인 경로는 플랫폼을 보내지 않으므로 null로 저장된다.
        pushWriteService.upsertByDeviceId("device-001", savedUser, "old-push-token");
        assertThat(pushReadService.findByDeviceId("device-001").orElseThrow().getPlatform()).isNull();

        DeviceTokenRegisterServiceRequest request = DeviceTokenRegisterServiceRequest.builder()
                .deviceId("device-001")
                .pushToken("new-push-token")
                .platform(DevicePlatform.IOS)
                .build();

        // when
        pushWriteService.registerDeviceToken(request);

        // then
        Optional<Push> result = pushReadService.findByDeviceId("device-001");
        assertThat(result).isPresent();
        assertAll(
                () -> assertThat(result.get().getPushToken()).isEqualTo("new-push-token"),
                () -> assertThat(result.get().getPlatform()).isEqualTo(DevicePlatform.IOS)
        );
    }
}
