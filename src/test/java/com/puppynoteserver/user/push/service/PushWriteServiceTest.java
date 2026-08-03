package com.puppynoteserver.user.push.service;

import com.puppynoteserver.IntegrationTestSupport;
import com.puppynoteserver.user.push.entity.Push;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
}
