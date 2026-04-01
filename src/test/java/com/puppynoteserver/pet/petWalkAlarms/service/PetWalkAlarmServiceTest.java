package com.puppynoteserver.pet.petWalkAlarms.service;

import com.puppynoteserver.IntegrationTestSupport;
import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.entity.enums.RoleType;
import com.puppynoteserver.pet.familyMembers.repository.FamilyMemberJpaRepository;
import com.puppynoteserver.pet.petWalkAlarms.entity.PetWalkAlarm;
import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmDay;
import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmStatus;
import com.puppynoteserver.pet.petWalkAlarms.repository.PetWalkAlarmJpaRepository;
import com.puppynoteserver.pet.petWalkAlarms.service.request.PetWalkAlarmCreateServiceRequest;
import com.puppynoteserver.pet.petWalkAlarms.service.request.PetWalkAlarmStatusUpdateServiceRequest;
import com.puppynoteserver.pet.petWalkAlarms.service.request.PetWalkAlarmUpdateServiceRequest;
import com.puppynoteserver.pet.petWalkAlarms.service.response.PetWalkAlarmResponse;
import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.pet.pets.repository.PetJpaRepository;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PetWalkAlarmServiceTest extends IntegrationTestSupport {

    @Autowired
    private PetWalkAlarmWriteService petWalkAlarmWriteService;

    @Autowired
    private PetWalkAlarmReadService petWalkAlarmReadService;

    @Autowired
    private PetWalkAlarmJpaRepository petWalkAlarmJpaRepository;

    @Autowired
    private PetJpaRepository petJpaRepository;

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    @AfterEach
    @Override
    public void tearDown() {
        petWalkAlarmJpaRepository.deleteAllInBatch();
        familyMemberJpaRepository.deleteAllInBatch();
        petJpaRepository.deleteAllInBatch();
        super.tearDown();
    }

    private Pet createAndSavePet(User user, String petName) {
        Pet pet = petJpaRepository.save(Pet.of(petName, LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(user, pet, RoleType.OWNER, FamilyMemberStatus.DONE));
        return pet;
    }

    // ==================== PetWalkAlarmWriteService - create ====================

    @DisplayName("펫 산책 알람을 정상적으로 생성한다.")
    @Test
    void 펫_산책_알람을_정상적으로_생성한다() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        PetWalkAlarmCreateServiceRequest request = PetWalkAlarmCreateServiceRequest.builder()
                .petId(pet.getId())
                .alarmStatus(AlarmStatus.YES)
                .alarmDays(Set.of(AlarmDay.MON, AlarmDay.WED, AlarmDay.FRI))
                .alarmTime(LocalTime.of(8, 0))
                .build();

        // when
        PetWalkAlarmResponse response = petWalkAlarmWriteService.create(request);

        // then
        assertThat(response.getAlarmId()).isNotNull();
        assertThat(response)
                .extracting("alarmStatus", "alarmTime")
                .containsExactly(AlarmStatus.YES, LocalTime.of(8, 0));
        assertThat(response.getAlarmDays()).containsExactlyInAnyOrder(AlarmDay.MON, AlarmDay.WED, AlarmDay.FRI);

        List<PetWalkAlarm> savedAlarms = petWalkAlarmJpaRepository.findByPetId(pet.getId());
        assertThat(savedAlarms)
                .hasSize(1)
                .extracting("alarmStatus")
                .containsExactly(AlarmStatus.YES);
    }

    @DisplayName("존재하지 않는 펫 ID로 알람 생성 시 NotFoundException이 발생한다.")
    @Test
    void 존재하지_않는_펫_ID로_알람_생성_시_예외가_발생한다() {
        // given
        PetWalkAlarmCreateServiceRequest request = PetWalkAlarmCreateServiceRequest.builder()
                .petId(999L)
                .alarmStatus(AlarmStatus.YES)
                .alarmDays(Set.of(AlarmDay.MON))
                .alarmTime(LocalTime.of(8, 0))
                .build();

        // when & then
        assertThatThrownBy(() -> petWalkAlarmWriteService.create(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("펫을 찾을 수 없습니다.");
    }

    // ==================== PetWalkAlarmWriteService - update ====================

    @DisplayName("펫 산책 알람을 정상적으로 수정한다.")
    @Test
    void 펫_산책_알람을_정상적으로_수정한다() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        PetWalkAlarm alarm = petWalkAlarmJpaRepository.save(
                PetWalkAlarm.of(pet, AlarmStatus.YES, Set.of(AlarmDay.MON), LocalTime.of(8, 0))
        );

        PetWalkAlarmUpdateServiceRequest request = PetWalkAlarmUpdateServiceRequest.builder()
                .alarmId(alarm.getId())
                .alarmStatus(AlarmStatus.NO)
                .alarmDays(Set.of(AlarmDay.TUE, AlarmDay.THU))
                .alarmTime(LocalTime.of(18, 30))
                .build();

        // when
        PetWalkAlarmResponse response = petWalkAlarmWriteService.update(request);

        // then
        assertThat(response)
                .extracting("alarmId", "alarmStatus", "alarmTime")
                .containsExactly(alarm.getId(), AlarmStatus.NO, LocalTime.of(18, 30));
        assertThat(response.getAlarmDays()).containsExactlyInAnyOrder(AlarmDay.TUE, AlarmDay.THU);

        PetWalkAlarm updatedAlarm = petWalkAlarmJpaRepository.findById(alarm.getId()).orElseThrow();
        assertThat(updatedAlarm.getAlarmStatus()).isEqualTo(AlarmStatus.NO);
    }

    @DisplayName("존재하지 않는 알람 ID로 수정 시 NotFoundException이 발생한다.")
    @Test
    void 존재하지_않는_알람_ID로_수정_시_예외가_발생한다() {
        // given
        PetWalkAlarmUpdateServiceRequest request = PetWalkAlarmUpdateServiceRequest.builder()
                .alarmId(999L)
                .alarmStatus(AlarmStatus.NO)
                .alarmDays(Set.of(AlarmDay.MON))
                .alarmTime(LocalTime.of(8, 0))
                .build();

        // when & then
        assertThatThrownBy(() -> petWalkAlarmWriteService.update(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("알람을 찾을 수 없습니다.");
    }

    // ==================== PetWalkAlarmWriteService - updateStatus ====================

    @DisplayName("펫 산책 알람 상태를 정상적으로 변경한다.")
    @Test
    void 펫_산책_알람_상태를_정상적으로_변경한다() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        PetWalkAlarm alarm = petWalkAlarmJpaRepository.save(
                PetWalkAlarm.of(pet, AlarmStatus.YES, Set.of(AlarmDay.MON), LocalTime.of(8, 0))
        );

        PetWalkAlarmStatusUpdateServiceRequest request = PetWalkAlarmStatusUpdateServiceRequest.builder()
                .alarmId(alarm.getId())
                .alarmStatus(AlarmStatus.NO)
                .build();

        // when
        PetWalkAlarmResponse response = petWalkAlarmWriteService.updateStatus(request);

        // then
        assertThat(response)
                .extracting("alarmId", "alarmStatus")
                .containsExactly(alarm.getId(), AlarmStatus.NO);

        PetWalkAlarm updatedAlarm = petWalkAlarmJpaRepository.findById(alarm.getId()).orElseThrow();
        assertThat(updatedAlarm.getAlarmStatus()).isEqualTo(AlarmStatus.NO);
    }

    @DisplayName("존재하지 않는 알람 ID로 상태 변경 시 NotFoundException이 발생한다.")
    @Test
    void 존재하지_않는_알람_ID로_상태_변경_시_예외가_발생한다() {
        // given
        PetWalkAlarmStatusUpdateServiceRequest request = PetWalkAlarmStatusUpdateServiceRequest.builder()
                .alarmId(999L)
                .alarmStatus(AlarmStatus.NO)
                .build();

        // when & then
        assertThatThrownBy(() -> petWalkAlarmWriteService.updateStatus(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("알람을 찾을 수 없습니다.");
    }

    // ==================== PetWalkAlarmWriteService - delete ====================

    @DisplayName("펫 산책 알람을 정상적으로 삭제한다.")
    @Test
    void 펫_산책_알람을_정상적으로_삭제한다() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        PetWalkAlarm alarm = petWalkAlarmJpaRepository.save(
                PetWalkAlarm.of(pet, AlarmStatus.YES, Set.of(AlarmDay.MON), LocalTime.of(8, 0))
        );
        Long alarmId = alarm.getId();

        // when
        petWalkAlarmWriteService.delete(alarmId);

        // then
        assertThat(petWalkAlarmJpaRepository.findById(alarmId)).isEmpty();
    }

    @DisplayName("존재하지 않는 알람 ID로 삭제 시 예외 없이 처리된다.")
    @Test
    void 존재하지_않는_알람_ID로_삭제_시_예외_없이_처리된다() {
        // when & then (존재하지 않을 경우 ifPresent이므로 예외 없이 통과)
        petWalkAlarmWriteService.delete(999L);
    }

    // ==================== PetWalkAlarmWriteService - deleteAllByPetId ====================

    @DisplayName("특정 펫의 모든 산책 알람을 삭제한다.")
    @Test
    void 특정_펫의_모든_산책_알람을_삭제한다() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        petWalkAlarmJpaRepository.save(
                PetWalkAlarm.of(pet, AlarmStatus.YES, Set.of(AlarmDay.MON), LocalTime.of(8, 0))
        );
        petWalkAlarmJpaRepository.save(
                PetWalkAlarm.of(pet, AlarmStatus.YES, Set.of(AlarmDay.WED), LocalTime.of(18, 0))
        );

        // when
        petWalkAlarmWriteService.deleteAllByPetId(pet.getId());

        // then
        List<PetWalkAlarm> remaining = petWalkAlarmJpaRepository.findByPetId(pet.getId());
        assertThat(remaining).isEmpty();
    }

    // ==================== PetWalkAlarmReadService - findById ====================

    @DisplayName("존재하는 알람 ID로 조회 시 알람 엔티티를 반환한다.")
    @Test
    void 존재하는_알람_ID로_조회_시_알람_엔티티를_반환한다() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        PetWalkAlarm alarm = petWalkAlarmJpaRepository.save(
                PetWalkAlarm.of(pet, AlarmStatus.YES, Set.of(AlarmDay.MON), LocalTime.of(8, 0))
        );

        // when
        PetWalkAlarm result = petWalkAlarmReadService.findById(alarm.getId());

        // then
        assertThat(result)
                .extracting("id", "alarmStatus")
                .containsExactly(alarm.getId(), AlarmStatus.YES);
    }

    @DisplayName("존재하지 않는 알람 ID로 조회 시 NotFoundException이 발생한다.")
    @Test
    void 존재하지_않는_알람_ID로_조회_시_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> petWalkAlarmReadService.findById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("알람을 찾을 수 없습니다.");
    }

    // ==================== PetWalkAlarmReadService - getAlarmsByPetId ====================

    @DisplayName("펫 ID로 산책 알람 목록을 정상적으로 조회한다.")
    @Test
    void 펫_ID로_산책_알람_목록을_정상적으로_조회한다() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        petWalkAlarmJpaRepository.save(
                PetWalkAlarm.of(pet, AlarmStatus.YES, Set.of(AlarmDay.MON, AlarmDay.WED), LocalTime.of(8, 0))
        );
        petWalkAlarmJpaRepository.save(
                PetWalkAlarm.of(pet, AlarmStatus.NO, Set.of(AlarmDay.SAT), LocalTime.of(18, 0))
        );

        // when
        List<PetWalkAlarmResponse> result = petWalkAlarmReadService.getAlarmsByPetId(pet.getId());

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("alarmStatus")
                .containsExactlyInAnyOrder(AlarmStatus.YES, AlarmStatus.NO);
    }

    @DisplayName("알람이 없는 펫은 빈 목록을 반환한다.")
    @Test
    void 알람이_없는_펫은_빈_목록을_반환한다() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        // when
        List<PetWalkAlarmResponse> result = petWalkAlarmReadService.getAlarmsByPetId(pet.getId());

        // then
        assertThat(result).isEmpty();
    }

    // ==================== PetWalkAlarmReadService - getTodayAlarmTimes ====================

    @DisplayName("오늘 요일에 해당하는 활성화된 알람 시간 목록을 조회한다.")
    @Test
    void 오늘_요일에_해당하는_활성화된_알람_시간_목록을_조회한다() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        // 오늘 요일을 구하여 알람 등록
        java.time.DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        AlarmDay today = AlarmDay.valueOf(dayOfWeek.name().substring(0, 3));

        LocalTime expectedTime = LocalTime.of(9, 0);
        petWalkAlarmJpaRepository.save(
                PetWalkAlarm.of(pet, AlarmStatus.YES, Set.of(today), expectedTime)
        );
        // 비활성화된 알람은 포함되지 않아야 함
        petWalkAlarmJpaRepository.save(
                PetWalkAlarm.of(pet, AlarmStatus.NO, Set.of(today), LocalTime.of(14, 0))
        );

        // when
        List<LocalTime> result = petWalkAlarmReadService.getTodayAlarmTimes(pet.getId());

        // then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(expectedTime);
    }

    @DisplayName("오늘 요일에 해당하는 알람이 없으면 빈 목록을 반환한다.")
    @Test
    void 오늘_요일에_해당하는_알람이_없으면_빈_목록을_반환한다() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        // 오늘 요일이 아닌 다른 요일로만 알람 등록
        java.time.DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        AlarmDay today = AlarmDay.valueOf(dayOfWeek.name().substring(0, 3));
        // 오늘 이외의 요일 찾기
        AlarmDay otherDay = (today == AlarmDay.MON) ? AlarmDay.TUE : AlarmDay.MON;

        petWalkAlarmJpaRepository.save(
                PetWalkAlarm.of(pet, AlarmStatus.YES, Set.of(otherDay), LocalTime.of(9, 0))
        );

        // when
        List<LocalTime> result = petWalkAlarmReadService.getTodayAlarmTimes(pet.getId());

        // then
        assertThat(result).isEmpty();
    }
}
