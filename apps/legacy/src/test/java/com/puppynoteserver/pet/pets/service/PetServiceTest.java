package com.puppynoteserver.pet.pets.service;

import com.puppynoteserver.IntegrationTestSupport;
import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.global.exception.PuppyNoteException;
import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.entity.enums.RoleType;
import com.puppynoteserver.pet.familyMembers.repository.FamilyMemberJpaRepository;
import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.pet.pets.repository.PetJpaRepository;
import com.puppynoteserver.pet.pets.service.request.PetCreateServiceRequest;
import com.puppynoteserver.pet.pets.service.request.PetUpdateServiceRequest;
import com.puppynoteserver.pet.pets.service.response.PetCreateResponse;
import com.puppynoteserver.pet.pets.service.response.PetResponse;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

class PetServiceTest extends IntegrationTestSupport {

    @Autowired
    private PetWriteService petWriteService;

    @Autowired
    private PetReadService petReadService;

    @Autowired
    private PetJpaRepository petJpaRepository;

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    @AfterEach
    @Override
    public void tearDown() {
        familyMemberJpaRepository.deleteAllInBatch();
        petJpaRepository.deleteAllInBatch();
        super.tearDown();
    }

    // ==================== PetWriteService - createPet ====================

    @DisplayName("펫을 생성하면 DB에 저장되고, 현재 유저가 OWNER로 등록된다.")
    @Test
    void 펫을_생성하면_DB에_저장되고_현재_유저가_OWNER로_등록된다() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        PetCreateServiceRequest request = PetCreateServiceRequest.builder()
                .name("초코")
                .birthDate(LocalDate.of(2020, 5, 1))
                .profileImage("profile/choco.jpg")
                .registrationNumber("REG-001")
                .build();

        // when
        PetCreateResponse response = petWriteService.createPet(request);

        // then
        assertThat(response.getPetId()).isNotNull();
        assertThat(response.getPetName()).isEqualTo("초코");

        Pet savedPet = petJpaRepository.findById(response.getPetId()).orElseThrow();
        assertThat(savedPet)
                .extracting("name", "birthDate", "registrationNumber")
                .containsExactly("초코", LocalDate.of(2020, 5, 1), "REG-001");

        Optional<FamilyMember> ownerRecord = familyMemberJpaRepository.findByIdUserIdAndIdPetId(user.getId(), savedPet.getId());
        assertThat(ownerRecord).isPresent();
        assertThat(ownerRecord.get())
                .extracting("role", "status")
                .containsExactly(RoleType.OWNER, FamilyMemberStatus.DONE);
    }

    @DisplayName("펫 생성 시 기존 가족(FAMILY) 유저도 자동으로 FAMILY로 등록된다.")
    @Test
    void 펫_생성_시_기존_가족_유저도_자동으로_FAMILY로_등록된다() {
        // given
        // owner1이 펫A를 소유하고, familyUser를 FAMILY로 초대해 DONE 상태
        User owner1 = userRepository.save(createUser("owner1@test.com", "password", SnsType.NORMAL));
        User familyUser = userRepository.save(createUser("family@test.com", "password", SnsType.NORMAL));

        Pet petA = petJpaRepository.save(Pet.of("펫A", LocalDate.of(2019, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(owner1, petA, RoleType.OWNER, FamilyMemberStatus.DONE));
        familyMemberJpaRepository.save(FamilyMember.of(familyUser, petA, RoleType.FAMILY, FamilyMemberStatus.DONE));

        // owner1이 새 펫B를 생성
        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(owner1.getId()));

        PetCreateServiceRequest request = PetCreateServiceRequest.builder()
                .name("펫B")
                .birthDate(LocalDate.of(2021, 3, 15))
                .profileImage(null)
                .registrationNumber(null)
                .build();

        // when
        PetCreateResponse response = petWriteService.createPet(request);

        // then
        assertThat(response.getPetName()).isEqualTo("펫B");

        Optional<FamilyMember> familyRecord = familyMemberJpaRepository.findByIdUserIdAndIdPetId(familyUser.getId(), response.getPetId());
        assertThat(familyRecord).isPresent();
        assertThat(familyRecord.get())
                .extracting("role", "status")
                .containsExactly(RoleType.FAMILY, FamilyMemberStatus.DONE);
    }

    // ==================== PetWriteService - updatePet ====================

    @DisplayName("펫 정보를 정상적으로 수정한다.")
    @Test
    void 펫_정보를_정상적으로_수정한다() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), "old-image.jpg", "OLD-REG"));
        familyMemberJpaRepository.save(FamilyMember.of(user, pet, RoleType.OWNER, FamilyMemberStatus.DONE));

        PetUpdateServiceRequest request = PetUpdateServiceRequest.builder()
                .name("코코")
                .birthDate(LocalDate.of(2021, 6, 10))
                .profileImage("new-image.jpg")
                .registrationNumber("NEW-REG")
                .build();

        // when
        petWriteService.updatePet(pet.getId(), request);

        // then
        Pet updatedPet = petJpaRepository.findById(pet.getId()).orElseThrow();
        assertThat(updatedPet)
                .extracting("name", "birthDate", "profileImage", "registrationNumber")
                .containsExactly("코코", LocalDate.of(2021, 6, 10), "new-image.jpg", "NEW-REG");
    }

    @DisplayName("존재하지 않는 펫 ID로 수정 시 NotFoundException이 발생한다.")
    @Test
    void 존재하지_않는_펫_ID로_수정_시_예외가_발생한다() {
        // given
        Long nonExistentPetId = 999L;
        PetUpdateServiceRequest request = PetUpdateServiceRequest.builder()
                .name("코코")
                .birthDate(LocalDate.of(2021, 6, 10))
                .profileImage(null)
                .registrationNumber(null)
                .build();

        // when & then
        assertThatThrownBy(() -> petWriteService.updatePet(nonExistentPetId, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("펫을 찾을 수 없습니다.");
    }

    // ==================== PetWriteService - deletePet ====================

    @DisplayName("OWNER 유저가 펫을 삭제하면 펫과 가족 레코드가 모두 제거된다.")
    @Test
    void OWNER_유저가_펫을_삭제하면_펫과_가족_레코드가_모두_제거된다() {
        // given
        User owner = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), "profile.jpg", null));
        familyMemberJpaRepository.save(FamilyMember.of(owner, pet, RoleType.OWNER, FamilyMemberStatus.DONE));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(owner.getId()));

        // when
        petWriteService.deletePet(pet.getId());

        // then
        assertThat(petJpaRepository.findById(pet.getId())).isEmpty();
        assertThat(familyMemberJpaRepository.findByIdUserIdAndIdPetId(owner.getId(), pet.getId())).isEmpty();
    }

    @DisplayName("FAMILY 유저가 펫 삭제를 시도하면 PuppyNoteException이 발생한다.")
    @Test
    void FAMILY_유저가_펫_삭제를_시도하면_예외가_발생한다() {
        // given
        User owner = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        User family = userRepository.save(createUser("family@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(owner, pet, RoleType.OWNER, FamilyMemberStatus.DONE));
        familyMemberJpaRepository.save(FamilyMember.of(family, pet, RoleType.FAMILY, FamilyMemberStatus.DONE));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(family.getId()));

        // when & then
        assertThatThrownBy(() -> petWriteService.deletePet(pet.getId()))
                .isInstanceOf(PuppyNoteException.class)
                .hasMessageContaining("펫 삭제는 OWNER만 가능합니다.");
    }

    @DisplayName("가족 구성원이 아닌 유저가 펫 삭제를 시도하면 NotFoundException이 발생한다.")
    @Test
    void 가족_구성원이_아닌_유저가_펫_삭제를_시도하면_예외가_발생한다() {
        // given
        User owner = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        User stranger = userRepository.save(createUser("stranger@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(owner, pet, RoleType.OWNER, FamilyMemberStatus.DONE));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(stranger.getId()));

        // when & then
        assertThatThrownBy(() -> petWriteService.deletePet(pet.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("해당 펫의 가족 구성원 정보를 찾을 수 없습니다.");
    }

    // ==================== PetReadService - getMyPets ====================

    @DisplayName("현재 로그인한 유저의 펫 목록을 조회한다.")
    @Test
    void 현재_로그인한_유저의_펫_목록을_조회한다() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet1 = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), "img1.jpg", null));
        Pet pet2 = petJpaRepository.save(Pet.of("코코", LocalDate.of(2021, 2, 2), "img2.jpg", null));
        familyMemberJpaRepository.save(FamilyMember.of(user, pet1, RoleType.OWNER, FamilyMemberStatus.DONE));
        familyMemberJpaRepository.save(FamilyMember.of(user, pet2, RoleType.FAMILY, FamilyMemberStatus.DONE));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        // when
        List<PetResponse> result = petReadService.getMyPets();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("petName")
                .containsExactlyInAnyOrder("초코", "코코");
    }

    @DisplayName("PENDING 상태의 가족 멤버는 내 펫 목록에 포함되지 않는다.")
    @Test
    void PENDING_상태의_가족_멤버는_내_펫_목록에_포함되지_않는다() {
        // given
        User owner = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        User pendingUser = userRepository.save(createUser("pending@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(owner, pet, RoleType.OWNER, FamilyMemberStatus.DONE));
        familyMemberJpaRepository.save(FamilyMember.of(pendingUser, pet, RoleType.FAMILY, FamilyMemberStatus.PENDING));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(pendingUser.getId()));

        // when
        List<PetResponse> result = petReadService.getMyPets();

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("가입된 펫이 없는 유저는 빈 목록을 반환한다.")
    @Test
    void 가입된_펫이_없는_유저는_빈_목록을_반환한다() {
        // given
        User user = userRepository.save(createUser("user@test.com", "password", SnsType.NORMAL));
        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        // when
        List<PetResponse> result = petReadService.getMyPets();

        // then
        assertThat(result).isEmpty();
    }

    // ==================== PetReadService - findById ====================

    @DisplayName("존재하는 펫 ID로 조회 시 펫 엔티티를 반환한다.")
    @Test
    void 존재하는_펫_ID로_조회_시_펫_엔티티를_반환한다() {
        // given
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));

        // when
        Pet result = petReadService.findById(pet.getId());

        // then
        assertThat(result)
                .extracting("id", "name")
                .containsExactly(pet.getId(), "초코");
    }

    @DisplayName("존재하지 않는 펫 ID로 조회 시 NotFoundException이 발생한다.")
    @Test
    void 존재하지_않는_펫_ID로_조회_시_예외가_발생한다() {
        // given
        Long nonExistentId = 999L;

        // when & then
        assertThatThrownBy(() -> petReadService.findById(nonExistentId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("펫을 찾을 수 없습니다.");
    }
}
