package com.puppynoteserver.pet.petItems.service;

import com.puppynoteserver.IntegrationTestSupport;
import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.entity.enums.RoleType;
import com.puppynoteserver.pet.familyMembers.repository.FamilyMemberJpaRepository;
import com.puppynoteserver.pet.petItemPurchase.repository.PetItemPurchaseJpaRepository;
import com.puppynoteserver.pet.petItems.entity.PetItem;
import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;
import com.puppynoteserver.pet.petItems.repository.PetItemJpaRepository;
import com.puppynoteserver.pet.petItems.service.request.PetItemCreateServiceRequest;
import com.puppynoteserver.pet.petItems.service.request.PetItemUpdateServiceRequest;
import com.puppynoteserver.pet.petItems.service.response.PetItemResponse;
import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.pet.pets.repository.PetJpaRepository;
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
import static org.assertj.core.api.Assertions.tuple;

class PetItemServiceTest extends IntegrationTestSupport {

    @Autowired
    private PetItemWriteService petItemWriteService;

    @Autowired
    private PetItemReadService petItemReadService;

    @Autowired
    private PetItemJpaRepository petItemJpaRepository;

    @Autowired
    private PetItemPurchaseJpaRepository petItemPurchaseJpaRepository;

    @Autowired
    private PetJpaRepository petJpaRepository;

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    @AfterEach
    @Override
    public void tearDown() {
        petItemPurchaseJpaRepository.deleteAllInBatch();
        petItemJpaRepository.deleteAllInBatch();
        familyMemberJpaRepository.deleteAllInBatch();
        petJpaRepository.deleteAllInBatch();
        super.tearDown();
    }

    // ==================== PetItemWriteService - create ====================

    @DisplayName("펫 용품을 정상적으로 생성한다.")
    @Test
    void 펫_용품을_정상적으로_생성한다() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(user, pet, RoleType.OWNER, FamilyMemberStatus.DONE));

        PetItemCreateServiceRequest request = PetItemCreateServiceRequest.builder()
                .petId(pet.getId())
                .name("사료")
                .category(ItemCategory.FOOD)
                .purchaseCycleDays(30)
                .purchaseUrl("https://example.com/food")
                .imageKey("pet-items/food.jpg")
                .build();

        // when
        PetItemResponse response = petItemWriteService.create(request);

        // then
        assertThat(response.getPetItemId()).isNotNull();
        assertThat(response)
                .extracting("name", "category", "purchaseCycleDays", "purchaseUrl")
                .containsExactly("사료", ItemCategory.FOOD, 30, "https://example.com/food");

        Optional<PetItem> savedItem = petItemJpaRepository.findById(response.getPetItemId());
        assertThat(savedItem).isPresent();
        assertThat(savedItem.get().getName()).isEqualTo("사료");
    }

    @DisplayName("존재하지 않는 펫 ID로 용품 생성 시 NotFoundException이 발생한다.")
    @Test
    void 존재하지_않는_펫_ID로_용품_생성_시_예외가_발생한다() {
        // given
        PetItemCreateServiceRequest request = PetItemCreateServiceRequest.builder()
                .petId(999L)
                .name("사료")
                .category(ItemCategory.FOOD)
                .purchaseCycleDays(30)
                .purchaseUrl(null)
                .imageKey(null)
                .build();

        // when & then
        assertThatThrownBy(() -> petItemWriteService.create(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("펫을 찾을 수 없습니다.");
    }

    // ==================== PetItemWriteService - update ====================

    @DisplayName("펫 용품 정보를 정상적으로 수정한다.")
    @Test
    void 펫_용품_정보를_정상적으로_수정한다() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(user, pet, RoleType.OWNER, FamilyMemberStatus.DONE));

        PetItem petItem = petItemJpaRepository.save(
                PetItem.of(pet, "사료", ItemCategory.FOOD, 30, "https://old.com", "old-key.jpg")
        );

        PetItemUpdateServiceRequest request = PetItemUpdateServiceRequest.builder()
                .name("간식")
                .category(ItemCategory.SNACK)
                .purchaseCycleDays(14)
                .purchaseUrl("https://new.com")
                .imageKey("new-key.jpg")
                .build();

        // when
        PetItemResponse response = petItemWriteService.update(petItem.getId(), request);

        // then
        assertThat(response)
                .extracting("name", "category", "purchaseCycleDays", "purchaseUrl")
                .containsExactly("간식", ItemCategory.SNACK, 14, "https://new.com");

        PetItem updatedItem = petItemJpaRepository.findById(petItem.getId()).orElseThrow();
        assertThat(updatedItem)
                .extracting("name", "category")
                .containsExactly("간식", ItemCategory.SNACK);
    }

    @DisplayName("존재하지 않는 용품 ID로 수정 시 NotFoundException이 발생한다.")
    @Test
    void 존재하지_않는_용품_ID로_수정_시_예외가_발생한다() {
        // given
        PetItemUpdateServiceRequest request = PetItemUpdateServiceRequest.builder()
                .name("간식")
                .category(ItemCategory.SNACK)
                .purchaseCycleDays(14)
                .purchaseUrl(null)
                .imageKey(null)
                .build();

        // when & then
        assertThatThrownBy(() -> petItemWriteService.update(999L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("용품 정보를 찾을 수 없습니다.");
    }

    // ==================== PetItemWriteService - delete ====================

    @DisplayName("펫 용품을 정상적으로 삭제한다.")
    @Test
    void 펫_용품을_정상적으로_삭제한다() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(user, pet, RoleType.OWNER, FamilyMemberStatus.DONE));

        PetItem petItem = petItemJpaRepository.save(
                PetItem.of(pet, "사료", ItemCategory.FOOD, 30, null, null)
        );
        Long petItemId = petItem.getId();

        // when
        petItemWriteService.delete(petItemId);

        // then
        assertThat(petItemJpaRepository.findById(petItemId)).isEmpty();
    }

    @DisplayName("존재하지 않는 용품 ID로 삭제 시 NotFoundException이 발생한다.")
    @Test
    void 존재하지_않는_용품_ID로_삭제_시_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> petItemWriteService.delete(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("용품 정보를 찾을 수 없습니다.");
    }

    // ==================== PetItemWriteService - deleteAllByPetId ====================

    @DisplayName("특정 펫의 모든 용품을 삭제한다.")
    @Test
    void 특정_펫의_모든_용품을_삭제한다() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(user, pet, RoleType.OWNER, FamilyMemberStatus.DONE));

        petItemJpaRepository.save(PetItem.of(pet, "사료", ItemCategory.FOOD, 30, null, null));
        petItemJpaRepository.save(PetItem.of(pet, "간식", ItemCategory.SNACK, 14, null, null));

        // when
        petItemWriteService.deleteAllByPetId(pet.getId());

        // then
        List<PetItem> remaining = petItemJpaRepository.findByPetId(pet.getId());
        assertThat(remaining).isEmpty();
    }

    // ==================== PetItemReadService - getItemsByPetId ====================

    @DisplayName("펫 ID로 카테고리 없이 전체 용품 목록을 조회한다.")
    @Test
    void 펫_ID로_카테고리_없이_전체_용품_목록을_조회한다() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(user, pet, RoleType.OWNER, FamilyMemberStatus.DONE));

        petItemJpaRepository.save(PetItem.of(pet, "사료", ItemCategory.FOOD, 30, null, null));
        petItemJpaRepository.save(PetItem.of(pet, "샴푸", ItemCategory.SHAMPOO, 60, null, null));

        // when
        List<PetItemResponse> result = petItemReadService.getItemsByPetId(pet.getId(), null);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("name")
                .containsExactlyInAnyOrder("사료", "샴푸");
    }

    @DisplayName("펫 ID와 카테고리로 필터링된 용품 목록을 조회한다.")
    @Test
    void 펫_ID와_카테고리로_필터링된_용품_목록을_조회한다() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(user, pet, RoleType.OWNER, FamilyMemberStatus.DONE));

        petItemJpaRepository.save(PetItem.of(pet, "사료", ItemCategory.FOOD, 30, null, null));
        petItemJpaRepository.save(PetItem.of(pet, "샴푸", ItemCategory.SHAMPOO, 60, null, null));

        // when
        List<PetItemResponse> result = petItemReadService.getItemsByPetId(pet.getId(), ItemCategory.FOOD);

        // then
        assertThat(result)
                .hasSize(1)
                .extracting("name", "category")
                .containsExactly(tuple("사료", ItemCategory.FOOD));
    }

    @DisplayName("용품이 없는 펫은 빈 목록을 반환한다.")
    @Test
    void 용품이_없는_펫은_빈_목록을_반환한다() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(user, pet, RoleType.OWNER, FamilyMemberStatus.DONE));

        // when
        List<PetItemResponse> result = petItemReadService.getItemsByPetId(pet.getId(), null);

        // then
        assertThat(result).isEmpty();
    }

    // ==================== PetItemReadService - countItemsByPetId ====================

    @DisplayName("펫 ID로 용품 개수를 정상적으로 조회한다.")
    @Test
    void 펫_ID로_용품_개수를_정상적으로_조회한다() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(user, pet, RoleType.OWNER, FamilyMemberStatus.DONE));

        petItemJpaRepository.save(PetItem.of(pet, "사료", ItemCategory.FOOD, 30, null, null));
        petItemJpaRepository.save(PetItem.of(pet, "샴푸", ItemCategory.SHAMPOO, 60, null, null));
        petItemJpaRepository.save(PetItem.of(pet, "간식", ItemCategory.SNACK, 14, null, null));

        // when
        long count = petItemReadService.countItemsByPetId(pet.getId());

        // then
        assertThat(count).isEqualTo(3);
    }

    // ==================== PetItemReadService - findById ====================

    @DisplayName("존재하는 용품 ID로 조회 시 용품 엔티티를 반환한다.")
    @Test
    void 존재하는_용품_ID로_조회_시_용품_엔티티를_반환한다() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(user, pet, RoleType.OWNER, FamilyMemberStatus.DONE));

        PetItem petItem = petItemJpaRepository.save(
                PetItem.of(pet, "사료", ItemCategory.FOOD, 30, null, null)
        );

        // when
        PetItem result = petItemReadService.findById(petItem.getId());

        // then
        assertThat(result)
                .extracting("id", "name")
                .containsExactly(petItem.getId(), "사료");
    }

    @DisplayName("존재하지 않는 용품 ID로 조회 시 NotFoundException이 발생한다.")
    @Test
    void 존재하지_않는_용품_ID로_조회_시_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> petItemReadService.findById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("용품 정보를 찾을 수 없습니다.");
    }

    // ==================== PetItemReadService - getItemDetail ====================

    @DisplayName("용품 상세 정보를 정상적으로 조회한다.")
    @Test
    void 용품_상세_정보를_정상적으로_조회한다() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(user, pet, RoleType.OWNER, FamilyMemberStatus.DONE));

        PetItem petItem = petItemJpaRepository.save(
                PetItem.of(pet, "사료", ItemCategory.FOOD, 30, "https://example.com/food", "food.jpg")
        );

        // when
        PetItemResponse response = petItemReadService.getItemDetail(petItem.getId());

        // then
        assertThat(response)
                .extracting("petItemId", "name", "category", "purchaseCycleDays", "purchaseUrl", "lastPurchasedAt", "nextPurchaseAt")
                .containsExactly(petItem.getId(), "사료", ItemCategory.FOOD, 30, "https://example.com/food", null, null);
    }

    @DisplayName("존재하지 않는 용품 ID로 상세 조회 시 NotFoundException이 발생한다.")
    @Test
    void 존재하지_않는_용품_ID로_상세_조회_시_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> petItemReadService.getItemDetail(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("용품 정보를 찾을 수 없습니다.");
    }
}
