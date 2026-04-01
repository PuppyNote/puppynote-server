package com.puppynoteserver.user.push.service;

import com.puppynoteserver.IntegrationTestSupport;
import com.puppynoteserver.user.push.entity.Push;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
public class PushReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private PushReadService pushReadService;

    @DisplayName("userId로 Push를 조회한다.")
    @Test
    void userId가_있을_때_findByUserId_Push를_반환한다() {
        // given
        User user = createUser("test@test.com", "1234", SnsType.NORMAL);
        User savedUser = userRepository.save(user);

        Push push = Push.of("device-001", savedUser, "push-token-001");
        pushRepository.save(push);

        // when
        Optional<Push> result = pushReadService.findByUserId(savedUser.getId());

        // then
        assertThat(result).isPresent();
        assertAll(
                () -> assertThat(result.get().getDeviceId()).isEqualTo("device-001"),
                () -> assertThat(result.get().getPushToken()).isEqualTo("push-token-001"),
                () -> assertThat(result.get().getUser().getId()).isEqualTo(savedUser.getId())
        );
    }

    @DisplayName("존재하지 않는 userId로 조회하면 빈 Optional을 반환한다.")
    @Test
    void userId가_없을_때_findByUserId_빈_Optional을_반환한다() {
        // given
        Long nonExistentUserId = 999L;

        // when
        Optional<Push> result = pushReadService.findByUserId(nonExistentUserId);

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("deviceId로 Push를 조회한다.")
    @Test
    void deviceId가_있을_때_findByDeviceId_Push를_반환한다() {
        // given
        User user = createUser("test@test.com", "1234", SnsType.NORMAL);
        User savedUser = userRepository.save(user);

        String deviceId = "device-001";
        Push push = Push.of(deviceId, savedUser, "push-token-001");
        pushRepository.save(push);

        // when
        Optional<Push> result = pushReadService.findByDeviceId(deviceId);

        // then
        assertThat(result).isPresent();
        assertAll(
                () -> assertThat(result.get().getDeviceId()).isEqualTo(deviceId),
                () -> assertThat(result.get().getPushToken()).isEqualTo("push-token-001"),
                () -> assertThat(result.get().getUser().getId()).isEqualTo(savedUser.getId())
        );
    }

    @DisplayName("존재하지 않는 deviceId로 조회하면 빈 Optional을 반환한다.")
    @Test
    void deviceId가_없을_때_findByDeviceId_빈_Optional을_반환한다() {
        // given
        String nonExistentDeviceId = "non-existent-device";

        // when
        Optional<Push> result = pushReadService.findByDeviceId(nonExistentDeviceId);

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("userId로 Push 목록을 전체 조회한다.")
    @Test
    void userId가_있을_때_findAllByUserId_Push_목록을_반환한다() {
        // given
        User user = createUser("test@test.com", "1234", SnsType.NORMAL);
        User savedUser = userRepository.save(user);

        Push push1 = Push.of("device-001", savedUser, "push-token-001");
        Push push2 = Push.of("device-002", savedUser, "push-token-002");
        pushRepository.save(push1);
        pushRepository.save(push2);

        // when
        List<Push> results = pushReadService.findAllByUserId(savedUser.getId());

        // then
        assertThat(results).hasSize(2)
                .extracting(Push::getDeviceId)
                .containsExactlyInAnyOrder("device-001", "device-002");
    }

    @DisplayName("Push가 없는 userId로 조회하면 빈 목록을 반환한다.")
    @Test
    void Push가_없을_때_findAllByUserId_빈_목록을_반환한다() {
        // given
        User user = createUser("test@test.com", "1234", SnsType.NORMAL);
        User savedUser = userRepository.save(user);

        // when
        List<Push> results = pushReadService.findAllByUserId(savedUser.getId());

        // then
        assertThat(results).isEmpty();
    }
}
