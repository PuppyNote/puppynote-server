package com.puppynoteserver.home.service;

import com.puppynoteserver.IntegrationTestSupport;
import com.puppynoteserver.home.service.response.HomeResponse;
import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.entity.enums.RoleType;
import com.puppynoteserver.pet.familyMembers.repository.FamilyMemberJpaRepository;
import com.puppynoteserver.pet.petItems.entity.PetItem;
import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;
import com.puppynoteserver.pet.petItems.repository.PetItemJpaRepository;
import com.puppynoteserver.pet.petWalkAlarms.entity.PetWalkAlarm;
import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmDay;
import com.puppynoteserver.pet.petWalkAlarms.entity.enums.AlarmStatus;
import com.puppynoteserver.pet.petWalkAlarms.repository.PetWalkAlarmJpaRepository;
import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.pet.pets.repository.PetJpaRepository;
import com.puppynoteserver.pet.walk.entity.Walk;
import com.puppynoteserver.pet.walk.repository.WalkJpaRepository;
import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class HomeReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private HomeReadService homeReadService;

    @Autowired
    private PetJpaRepository petJpaRepository;

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    @Autowired
    private WalkJpaRepository walkJpaRepository;

    @Autowired
    private PetItemJpaRepository petItemJpaRepository;

    @Autowired
    private PetWalkAlarmJpaRepository petWalkAlarmJpaRepository;

    @AfterEach
    @Override
    public void tearDown() {
        petWalkAlarmJpaRepository.deleteAllInBatch();
        petItemJpaRepository.deleteAllInBatch();
        walkJpaRepository.deleteAllInBatch();
        familyMemberJpaRepository.deleteAllInBatch();
        petJpaRepository.deleteAllInBatch();
        super.tearDown();
    }

    @DisplayName("홈 정보를 정상적으로 조회한다.")
    @Test
    void 펫_ID로_홈_정보를_정상적으로_조회한다() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(
                Pet.of("멍멍이", LocalDate.of(2020, 1, 1), "profile.jpg", "1234567890"));
        familyMemberJpaRepository.save(
                FamilyMember.of(user, pet, RoleType.OWNER, FamilyMemberStatus.DONE));

        given(s3StorageService.getCloudFrontUrl(any(), any()))
                .willReturn("https://cdn.example.com/profile.jpg");

        // when
        HomeResponse response = homeReadService.getHomeInfo(pet.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response)
                .extracting("petName", "petProfileImageUrl", "birthDate")
                .containsExactly("멍멍이", "https://cdn.example.com/profile.jpg", LocalDate.of(2020, 1, 1));
    }

    @DisplayName("생일이 있는 펫의 나이와 D-day를 계산한다.")
    @Test
    void 생일이_있는_펫의_나이와_D_day를_계산한다() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        // 2년 전 생일로 설정
        LocalDate birthDate = LocalDate.now().minusYears(2);
        Pet pet = petJpaRepository.save(
                Pet.of("나이계산강아지", birthDate, "profile.jpg", null));
        familyMemberJpaRepository.save(
                FamilyMember.of(user, pet, RoleType.OWNER, FamilyMemberStatus.DONE));

        given(s3StorageService.getCloudFrontUrl(any(), any()))
                .willReturn("https://cdn.example.com/profile.jpg");

        // when
        HomeResponse response = homeReadService.getHomeInfo(pet.getId());

        // then
        assertThat(response.getPetAge()).isEqualTo("2살");
        assertThat(response.getBirthdayDday()).isNotNull();
        assertThat(response.getBirthdayDday()).isGreaterThanOrEqualTo(0);
    }

    @DisplayName("생일이 null인 펫의 나이와 D-day는 null이다.")
    @Test
    void 생일이_null인_펫의_나이와_D_day는_null이다() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(
                Pet.of("생일없는강아지", null, "profile.jpg", null));
        familyMemberJpaRepository.save(
                FamilyMember.of(user, pet, RoleType.OWNER, FamilyMemberStatus.DONE));

        given(s3StorageService.getCloudFrontUrl(any(), any()))
                .willReturn("https://cdn.example.com/profile.jpg");

        // when
        HomeResponse response = homeReadService.getHomeInfo(pet.getId());

        // then
        assertThat(response.getPetAge()).isNull();
        assertThat(response.getBirthdayDday()).isNull();
    }

    @DisplayName("오늘 산책한 펫의 walkedToday는 true이다.")
    @Test
    void 오늘_산책한_펫의_walkedToday는_true이다() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(
                Pet.of("산책한강아지", LocalDate.of(2020, 6, 15), "profile.jpg", null));
        familyMemberJpaRepository.save(
                FamilyMember.of(user, pet, RoleType.OWNER, FamilyMemberStatus.DONE));

        LocalDateTime now = LocalDateTime.now();
        walkJpaRepository.save(Walk.of(pet, now.minusHours(1), now,
                BigDecimal.valueOf(37.5), BigDecimal.valueOf(127.0), "서울", "오늘 산책"));

        given(s3StorageService.getCloudFrontUrl(any(), any()))
                .willReturn("https://cdn.example.com/profile.jpg");

        // when
        HomeResponse response = homeReadService.getHomeInfo(pet.getId());

        // then
        assertThat(response.isWalkedToday()).isTrue();
    }

    @DisplayName("오늘 산책하지 않은 펫의 walkedToday는 false이다.")
    @Test
    void 오늘_산책하지_않은_펫의_walkedToday는_false이다() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(
                Pet.of("산책안한강아지", LocalDate.of(2020, 6, 15), "profile.jpg", null));
        familyMemberJpaRepository.save(
                FamilyMember.of(user, pet, RoleType.OWNER, FamilyMemberStatus.DONE));

        given(s3StorageService.getCloudFrontUrl(any(), any()))
                .willReturn("https://cdn.example.com/profile.jpg");

        // when
        HomeResponse response = homeReadService.getHomeInfo(pet.getId());

        // then
        assertThat(response.isWalkedToday()).isFalse();
    }

    @DisplayName("펫 용품 수를 정상적으로 조회한다.")
    @Test
    void 펫_용품_수를_정상적으로_조회한다() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(
                Pet.of("용품있는강아지", LocalDate.of(2020, 6, 15), "profile.jpg", null));
        familyMemberJpaRepository.save(
                FamilyMember.of(user, pet, RoleType.OWNER, FamilyMemberStatus.DONE));

        petItemJpaRepository.save(PetItem.of(pet, "사료", ItemCategory.FOOD, 30, null, null));
        petItemJpaRepository.save(PetItem.of(pet, "간식", ItemCategory.SNACK, 14, null, null));

        given(s3StorageService.getCloudFrontUrl(any(), any()))
                .willReturn("https://cdn.example.com/profile.jpg");

        // when
        HomeResponse response = homeReadService.getHomeInfo(pet.getId());

        // then
        assertThat(response.getPetItemCount()).isEqualTo(2);
    }

    @DisplayName("오늘의 산책 알람 시간 목록을 조회한다.")
    @Test
    void 오늘의_산책_알람_시간_목록을_조회한다() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(
                Pet.of("알람있는강아지", LocalDate.of(2020, 6, 15), "profile.jpg", null));
        familyMemberJpaRepository.save(
                FamilyMember.of(user, pet, RoleType.OWNER, FamilyMemberStatus.DONE));

        // 모든 요일에 알람 설정
        Set<AlarmDay> allDays = Set.of(AlarmDay.MON, AlarmDay.TUE, AlarmDay.WED,
                AlarmDay.THU, AlarmDay.FRI, AlarmDay.SAT, AlarmDay.SUN);
        petWalkAlarmJpaRepository.save(
                PetWalkAlarm.of(pet, AlarmStatus.YES, allDays, LocalTime.of(9, 0)));

        given(s3StorageService.getCloudFrontUrl(any(), any()))
                .willReturn("https://cdn.example.com/profile.jpg");

        // when
        HomeResponse response = homeReadService.getHomeInfo(pet.getId());

        // then
        assertThat(response.getTodayWalkAlarmTimes()).isNotEmpty();
        assertThat(response.getTodayWalkAlarmTimes()).contains(LocalTime.of(9, 0));
    }

    @DisplayName("존재하지 않는 펫 ID로 조회 시 예외가 발생한다.")
    @Test
    void 존재하지_않는_펫_ID로_조회_시_예외가_발생한다() {
        // given
        Long nonExistentPetId = 999L;

        // when & then
        assertThatThrownBy(() -> homeReadService.getHomeInfo(nonExistentPetId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("펫을 찾을 수 없습니다.");
    }

    @DisplayName("최근 7일 산책 횟수를 정상적으로 집계한다.")
    @Test
    void 최근_7일_산책_횟수를_정상적으로_집계한다() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(
                Pet.of("산책많이한강아지", LocalDate.of(2020, 6, 15), "profile.jpg", null));
        familyMemberJpaRepository.save(
                FamilyMember.of(user, pet, RoleType.OWNER, FamilyMemberStatus.DONE));

        // 최근 7일 내 산책 2회
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        walkJpaRepository.save(Walk.of(pet, threeDaysAgo, threeDaysAgo.plusHours(1),
                null, null, null, null));
        walkJpaRepository.save(Walk.of(pet, twoDaysAgo, twoDaysAgo.plusHours(1),
                null, null, null, null));

        given(s3StorageService.getCloudFrontUrl(any(), any()))
                .willReturn("https://cdn.example.com/profile.jpg");

        // when
        HomeResponse response = homeReadService.getHomeInfo(pet.getId());

        // then
        assertThat(response.getRecentWalkCount()).isEqualTo(2);
    }
}
