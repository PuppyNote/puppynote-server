package com.puppynoteserver.pet.walk.service;

import com.puppynoteserver.IntegrationTestSupport;
import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.entity.enums.RoleType;
import com.puppynoteserver.pet.familyMembers.repository.FamilyMemberJpaRepository;
import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.pet.pets.repository.PetJpaRepository;
import com.puppynoteserver.pet.walk.entity.Walk;
import com.puppynoteserver.pet.walk.repository.WalkJpaRepository;
import com.puppynoteserver.pet.walk.service.request.WalkCreateServiceRequest;
import com.puppynoteserver.pet.walk.service.response.WalkCalendarResponse;
import com.puppynoteserver.pet.walk.service.response.WalkDetailResponse;
import com.puppynoteserver.pet.walk.service.response.WalkResponse;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WalkServiceTest extends IntegrationTestSupport {

    @Autowired
    private WalkWriteService walkWriteService;

    @Autowired
    private WalkReadService walkReadService;

    @Autowired
    private WalkJpaRepository walkJpaRepository;

    @Autowired
    private PetJpaRepository petJpaRepository;

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    @AfterEach
    @Override
    public void tearDown() {
        // CascadeType.ALL로 photos도 함께 삭제됨
        walkJpaRepository.deleteAll(walkJpaRepository.findAll());
        familyMemberJpaRepository.deleteAllInBatch();
        petJpaRepository.deleteAllInBatch();
        super.tearDown();
    }

    private Pet createAndSavePet(User user, String petName) {
        Pet pet = petJpaRepository.save(Pet.of(petName, LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(user, pet, RoleType.OWNER, FamilyMemberStatus.DONE));
        return pet;
    }

    // ==================== WalkWriteService - create ====================

    @DisplayName("산책 기록을 정상적으로 생성한다.")
    @Test
    void createWalk_success() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        LocalDateTime startTime = LocalDateTime.of(2024, 6, 10, 9, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 6, 10, 9, 30);

        WalkCreateServiceRequest request = WalkCreateServiceRequest.builder()
                .petId(pet.getId())
                .startTime(startTime)
                .endTime(endTime)
                .latitude(new BigDecimal("37.5665151"))
                .longitude(new BigDecimal("126.9779451"))
                .location("서울 중구")
                .memo("오늘 날씨 좋음")
                .photoKeys(null)
                .build();

        // when
        WalkResponse response = walkWriteService.create(request);

        // then
        assertThat(response.getWalkId()).isNotNull();
        assertThat(response)
                .extracting("petId", "startTime", "endTime", "location", "memo", "photoUrl")
                .containsExactly(pet.getId(), startTime, endTime, "서울 중구", "오늘 날씨 좋음", null);

        List<Walk> savedWalks = walkJpaRepository.findAllByPetId(pet.getId());
        assertThat(savedWalks).hasSize(1);
    }

    @DisplayName("사진 키와 함께 산책 기록을 생성하면 첫 번째 사진 URL이 응답에 포함된다.")
    @Test
    void createWalk_withPhotos() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        LocalDateTime startTime = LocalDateTime.of(2024, 6, 10, 9, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 6, 10, 9, 30);

        WalkCreateServiceRequest request = WalkCreateServiceRequest.builder()
                .petId(pet.getId())
                .startTime(startTime)
                .endTime(endTime)
                .latitude(null)
                .longitude(null)
                .location(null)
                .memo(null)
                .photoKeys(List.of("walks/photo1.jpg", "walks/photo2.jpg"))
                .build();

        // when
        WalkResponse response = walkWriteService.create(request);

        // then
        assertThat(response.getWalkId()).isNotNull();

        Walk savedWalk = walkJpaRepository.findAllByPetId(pet.getId()).get(0);
        assertThat(savedWalk.getPhotos()).hasSize(2);
    }

    @DisplayName("존재하지 않는 펫 ID로 산책 기록 생성 시 NotFoundException이 발생한다.")
    @Test
    void createWalk_petNotFound() {
        // given
        WalkCreateServiceRequest request = WalkCreateServiceRequest.builder()
                .petId(999L)
                .startTime(LocalDateTime.of(2024, 6, 10, 9, 0))
                .endTime(LocalDateTime.of(2024, 6, 10, 9, 30))
                .latitude(null)
                .longitude(null)
                .location(null)
                .memo(null)
                .photoKeys(null)
                .build();

        // when & then
        assertThatThrownBy(() -> walkWriteService.create(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("펫을 찾을 수 없습니다.");
    }

    // ==================== WalkWriteService - delete ====================

    @DisplayName("산책 기록을 정상적으로 삭제한다.")
    @Test
    void deleteWalk_success() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        Walk walk = walkJpaRepository.save(
                Walk.of(pet,
                        LocalDateTime.of(2024, 6, 10, 9, 0),
                        LocalDateTime.of(2024, 6, 10, 9, 30),
                        null, null, null, null)
        );
        Long walkId = walk.getId();

        // when
        walkWriteService.delete(walkId);

        // then
        assertThat(walkJpaRepository.findById(walkId)).isEmpty();
    }

    @DisplayName("존재하지 않는 산책 기록 ID로 삭제 시 예외가 발생한다.")
    @Test
    void deleteWalk_notFound() {
        // when & then
        assertThatThrownBy(() -> walkWriteService.delete(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("산책 기록을 찾을 수 없습니다.");
    }

    // ==================== WalkWriteService - deleteAllByPetId ====================

    @DisplayName("특정 펫의 모든 산책 기록을 삭제한다.")
    @Test
    void deleteAllByPetId_success() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        walkJpaRepository.save(Walk.of(pet,
                LocalDateTime.of(2024, 6, 10, 9, 0),
                LocalDateTime.of(2024, 6, 10, 9, 30),
                null, null, null, null));
        walkJpaRepository.save(Walk.of(pet,
                LocalDateTime.of(2024, 6, 11, 9, 0),
                LocalDateTime.of(2024, 6, 11, 9, 45),
                null, null, null, null));

        // when
        walkWriteService.deleteAllByPetId(pet.getId());

        // then
        List<Walk> remaining = walkJpaRepository.findAllByPetId(pet.getId());
        assertThat(remaining).isEmpty();
    }

    // ==================== WalkReadService - getWalksByPetId ====================

    @DisplayName("특정 날짜의 펫 산책 기록 목록을 조회한다.")
    @Test
    void getWalksByPetId_success() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        LocalDate targetDate = LocalDate.of(2024, 6, 10);

        walkJpaRepository.save(Walk.of(pet,
                LocalDateTime.of(2024, 6, 10, 9, 0),
                LocalDateTime.of(2024, 6, 10, 9, 30),
                null, null, "서울", null));
        walkJpaRepository.save(Walk.of(pet,
                LocalDateTime.of(2024, 6, 10, 18, 0),
                LocalDateTime.of(2024, 6, 10, 18, 40),
                null, null, "한강", null));
        // 다른 날짜 산책 (조회에 포함되지 않아야 함)
        walkJpaRepository.save(Walk.of(pet,
                LocalDateTime.of(2024, 6, 11, 9, 0),
                LocalDateTime.of(2024, 6, 11, 9, 30),
                null, null, null, null));

        // when
        List<WalkResponse> result = walkReadService.getWalksByPetId(pet.getId(), targetDate);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("location")
                .containsExactlyInAnyOrder("서울", "한강");
    }

    @DisplayName("해당 날짜에 산책 기록이 없으면 빈 목록을 반환한다.")
    @Test
    void getWalksByPetId_empty() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        LocalDate targetDate = LocalDate.of(2024, 6, 10);

        // when
        List<WalkResponse> result = walkReadService.getWalksByPetId(pet.getId(), targetDate);

        // then
        assertThat(result).isEmpty();
    }

    // ==================== WalkReadService - getWalkCalendar ====================

    @DisplayName("특정 월의 산책 달력을 조회한다.")
    @Test
    void getWalkCalendar_success() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        YearMonth yearMonth = YearMonth.of(2024, 6);

        walkJpaRepository.save(Walk.of(pet,
                LocalDateTime.of(2024, 6, 5, 9, 0),
                LocalDateTime.of(2024, 6, 5, 9, 30),
                null, null, null, null));
        walkJpaRepository.save(Walk.of(pet,
                LocalDateTime.of(2024, 6, 15, 9, 0),
                LocalDateTime.of(2024, 6, 15, 9, 30),
                null, null, null, null));

        // when
        List<WalkCalendarResponse> result = walkReadService.getWalkCalendar(pet.getId(), yearMonth);

        // then
        assertThat(result).hasSize(30); // 6월은 30일
        WalkCalendarResponse june5 = result.stream()
                .filter(r -> r.getDate().equals(LocalDate.of(2024, 6, 5)))
                .findFirst().orElseThrow();
        assertThat(june5.isHasWalk()).isTrue();

        WalkCalendarResponse june10 = result.stream()
                .filter(r -> r.getDate().equals(LocalDate.of(2024, 6, 10)))
                .findFirst().orElseThrow();
        assertThat(june10.isHasWalk()).isFalse();
    }

    // ==================== WalkReadService - countRecentWalks ====================

    @DisplayName("특정 기간의 산책 횟수를 정상적으로 조회한다.")
    @Test
    void countRecentWalks_success() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        walkJpaRepository.save(Walk.of(pet,
                LocalDateTime.of(2024, 6, 5, 9, 0),
                LocalDateTime.of(2024, 6, 5, 9, 30),
                null, null, null, null));
        walkJpaRepository.save(Walk.of(pet,
                LocalDateTime.of(2024, 6, 10, 9, 0),
                LocalDateTime.of(2024, 6, 10, 9, 30),
                null, null, null, null));
        // 기간 외 산책
        walkJpaRepository.save(Walk.of(pet,
                LocalDateTime.of(2024, 7, 1, 9, 0),
                LocalDateTime.of(2024, 7, 1, 9, 30),
                null, null, null, null));

        LocalDate from = LocalDate.of(2024, 6, 1);
        LocalDate to = LocalDate.of(2024, 6, 30);

        // when
        long count = walkReadService.countRecentWalks(pet.getId(), from, to);

        // then
        assertThat(count).isEqualTo(2);
    }

    // ==================== WalkReadService - walkedToday ====================

    @DisplayName("오늘 산책 기록이 있으면 true를 반환한다.")
    @Test
    void walkedToday_returnsTrue() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        LocalDate today = LocalDate.now();
        walkJpaRepository.save(Walk.of(pet,
                today.atTime(9, 0),
                today.atTime(9, 30),
                null, null, null, null));

        // when
        boolean result = walkReadService.walkedToday(pet.getId());

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("오늘 산책 기록이 없으면 false를 반환한다.")
    @Test
    void walkedToday_returnsFalse() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        // 어제 산책
        LocalDate yesterday = LocalDate.now().minusDays(1);
        walkJpaRepository.save(Walk.of(pet,
                yesterday.atTime(9, 0),
                yesterday.atTime(9, 30),
                null, null, null, null));

        // when
        boolean result = walkReadService.walkedToday(pet.getId());

        // then
        assertThat(result).isFalse();
    }

    // ==================== WalkReadService - daysSinceLastWalk ====================

    @DisplayName("마지막 산책 이후 경과 일수를 정상적으로 조회한다.")
    @Test
    void daysSinceLastWalk_success() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        LocalDate threeDaysAgo = LocalDate.now().minusDays(3);
        walkJpaRepository.save(Walk.of(pet,
                threeDaysAgo.atTime(9, 0),
                threeDaysAgo.atTime(9, 30),
                null, null, null, null));

        // when
        Integer result = walkReadService.daysSinceLastWalk(pet.getId());

        // then
        assertThat(result).isEqualTo(3);
    }

    @DisplayName("산책 기록이 없으면 null을 반환한다.")
    @Test
    void daysSinceLastWalk_noWalk() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        // when
        Integer result = walkReadService.daysSinceLastWalk(pet.getId());

        // then
        assertThat(result).isNull();
    }

    // ==================== WalkReadService - monthlyWalkMinutes ====================

    @DisplayName("이번 달 총 산책 시간(분)을 정상적으로 조회한다.")
    @Test
    void monthlyWalkMinutes_success() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);

        // 이번 달 산책 2회: 30분 + 45분 = 75분
        walkJpaRepository.save(Walk.of(pet,
                firstDayOfMonth.atTime(9, 0),
                firstDayOfMonth.atTime(9, 30),
                null, null, null, null));
        walkJpaRepository.save(Walk.of(pet,
                firstDayOfMonth.plusDays(1).atTime(18, 0),
                firstDayOfMonth.plusDays(1).atTime(18, 45),
                null, null, null, null));

        // when
        long result = walkReadService.monthlyWalkMinutes(pet.getId());

        // then
        assertThat(result).isEqualTo(75);
    }

    // ==================== WalkReadService - findById ====================

    @DisplayName("존재하는 산책 기록 ID로 조회 시 Walk 엔티티를 반환한다.")
    @Test
    void findById_success() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        Walk walk = walkJpaRepository.save(Walk.of(pet,
                LocalDateTime.of(2024, 6, 10, 9, 0),
                LocalDateTime.of(2024, 6, 10, 9, 30),
                null, null, "서울", "메모"));

        // when
        Walk result = walkReadService.findById(walk.getId());

        // then
        assertThat(result)
                .extracting("id", "location")
                .containsExactly(walk.getId(), "서울");
    }

    @DisplayName("존재하지 않는 산책 기록 ID로 조회 시 NotFoundException이 발생한다.")
    @Test
    void findById_notFound() {
        // when & then
        assertThatThrownBy(() -> walkReadService.findById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("산책 기록을 찾을 수 없습니다.");
    }

    // ==================== WalkReadService - getWalkDetail ====================

    @DisplayName("산책 상세 정보를 정상적으로 조회한다.")
    @Test
    void getWalkDetail_success() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");

        LocalDateTime startTime = LocalDateTime.of(2024, 6, 10, 9, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 6, 10, 9, 30);

        Walk walk = walkJpaRepository.save(Walk.of(pet,
                startTime, endTime,
                new BigDecimal("37.5665151"),
                new BigDecimal("126.9779451"),
                "서울 중구", "즐거운 산책"));

        // when
        WalkDetailResponse response = walkReadService.getWalkDetail(walk.getId());

        // then
        assertThat(response)
                .extracting("walkId", "location", "memo", "startTime", "endTime")
                .containsExactly(walk.getId(), "서울 중구", "즐거운 산책", startTime, endTime);
        assertThat(response.getPhotoUrls()).isEmpty();
    }

    @DisplayName("존재하지 않는 산책 기록 ID로 상세 조회 시 NotFoundException이 발생한다.")
    @Test
    void getWalkDetail_notFound() {
        // when & then
        assertThatThrownBy(() -> walkReadService.getWalkDetail(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("산책 기록을 찾을 수 없습니다.");
    }
}
