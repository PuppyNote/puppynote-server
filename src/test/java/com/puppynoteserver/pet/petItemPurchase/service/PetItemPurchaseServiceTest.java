package com.puppynoteserver.pet.petItemPurchase.service;

import com.puppynoteserver.IntegrationTestSupport;
import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.entity.enums.RoleType;
import com.puppynoteserver.pet.familyMembers.repository.FamilyMemberJpaRepository;
import com.puppynoteserver.pet.petItemPurchase.entity.PetItemPurchase;
import com.puppynoteserver.pet.petItemPurchase.repository.PetItemPurchaseJpaRepository;
import com.puppynoteserver.pet.petItemPurchase.service.request.PetItemPurchaseCreateServiceRequest;
import com.puppynoteserver.pet.petItemPurchase.service.response.PetItemPurchaseResponse;
import com.puppynoteserver.pet.petItems.entity.PetItem;
import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;
import com.puppynoteserver.pet.petItems.repository.PetItemJpaRepository;
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
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PetItemPurchaseServiceTest extends IntegrationTestSupport {

    @Autowired
    private PetItemPurchaseWriteService petItemPurchaseWriteService;

    @Autowired
    private PetItemPurchaseReadService petItemPurchaseReadService;

    @Autowired
    private PetItemPurchaseJpaRepository petItemPurchaseJpaRepository;

    @Autowired
    private PetItemJpaRepository petItemJpaRepository;

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

    private Pet createAndSavePet(User user, String petName) {
        Pet pet = petJpaRepository.save(Pet.of(petName, LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(user, pet, RoleType.OWNER, FamilyMemberStatus.DONE));
        return pet;
    }

    private PetItem createAndSavePetItem(Pet pet, String name, ItemCategory category) {
        return petItemJpaRepository.save(PetItem.of(pet, name, category, 30, null, null));
    }

    // ==================== PetItemPurchaseWriteService - recordPurchase ====================

    @DisplayName("펫 용품 구매 이력을 정상적으로 등록한다.")
    @Test
    void recordPurchase_success() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");
        PetItem petItem = createAndSavePetItem(pet, "사료", ItemCategory.FOOD);

        LocalDate purchasedDate = LocalDate.of(2024, 1, 15);
        PetItemPurchaseCreateServiceRequest request = PetItemPurchaseCreateServiceRequest.builder()
                .petItemId(petItem.getId())
                .purchasedAt(purchasedDate)
                .build();

        // when
        PetItemPurchaseResponse response = petItemPurchaseWriteService.recordPurchase(request);

        // then
        assertThat(response.getId()).isNotNull();
        assertThat(response.getPetItemId()).isEqualTo(petItem.getId());
        assertThat(response.getPurchasedAt()).isEqualTo(purchasedDate);

        List<PetItemPurchase> purchases = petItemPurchaseJpaRepository.findAllByPetItemIdOrderByPurchasedAtDesc(petItem.getId());
        assertThat(purchases).hasSize(1);
        assertThat(purchases.get(0).getPurchasedAt()).isEqualTo(purchasedDate);
    }

    @DisplayName("존재하지 않는 용품 ID로 구매 이력 등록 시 NotFoundException이 발생한다.")
    @Test
    void recordPurchase_petItemNotFound() {
        // given
        PetItemPurchaseCreateServiceRequest request = PetItemPurchaseCreateServiceRequest.builder()
                .petItemId(999L)
                .purchasedAt(LocalDate.of(2024, 1, 15))
                .build();

        // when & then
        assertThatThrownBy(() -> petItemPurchaseWriteService.recordPurchase(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("용품 정보를 찾을 수 없습니다.");
    }

    // ==================== PetItemPurchaseWriteService - deletePurchase ====================

    @DisplayName("구매 이력을 정상적으로 삭제한다.")
    @Test
    void deletePurchase_success() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");
        PetItem petItem = createAndSavePetItem(pet, "사료", ItemCategory.FOOD);

        PetItemPurchase purchase = petItemPurchaseJpaRepository.save(
                PetItemPurchase.of(petItem, LocalDate.of(2024, 1, 15))
        );
        Long purchaseId = purchase.getId();

        // when
        petItemPurchaseWriteService.deletePurchase(purchaseId);

        // then
        assertThat(petItemPurchaseJpaRepository.findById(purchaseId)).isEmpty();
    }

    @DisplayName("존재하지 않는 구매 이력 ID로 삭제 시 NotFoundException이 발생한다.")
    @Test
    void deletePurchase_notFound() {
        // when & then
        assertThatThrownBy(() -> petItemPurchaseWriteService.deletePurchase(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("구매 이력을 찾을 수 없습니다.");
    }

    // ==================== PetItemPurchaseWriteService - deleteAllByPetItemId ====================

    @DisplayName("특정 용품의 모든 구매 이력을 삭제한다.")
    @Test
    void deleteAllByPetItemId_success() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");
        PetItem petItem = createAndSavePetItem(pet, "사료", ItemCategory.FOOD);

        petItemPurchaseJpaRepository.save(PetItemPurchase.of(petItem, LocalDate.of(2024, 1, 15)));
        petItemPurchaseJpaRepository.save(PetItemPurchase.of(petItem, LocalDate.of(2024, 2, 15)));

        // when
        petItemPurchaseWriteService.deleteAllByPetItemId(petItem.getId());

        // then
        List<PetItemPurchase> remaining = petItemPurchaseJpaRepository.findAllByPetItemIdOrderByPurchasedAtDesc(petItem.getId());
        assertThat(remaining).isEmpty();
    }

    // ==================== PetItemPurchaseReadService - getPurchaseHistory ====================

    @DisplayName("용품 ID로 구매 이력 목록을 정상적으로 조회한다.")
    @Test
    void getPurchaseHistory_success() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");
        PetItem petItem = createAndSavePetItem(pet, "사료", ItemCategory.FOOD);

        petItemPurchaseJpaRepository.save(PetItemPurchase.of(petItem, LocalDate.of(2024, 1, 15)));
        petItemPurchaseJpaRepository.save(PetItemPurchase.of(petItem, LocalDate.of(2024, 2, 15)));

        // when
        List<PetItemPurchaseResponse> result = petItemPurchaseReadService.getPurchaseHistory(petItem.getId());

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("petItemId")
                .containsOnly(petItem.getId());
    }

    @DisplayName("구매 이력이 없는 용품은 빈 목록을 반환한다.")
    @Test
    void getPurchaseHistory_empty() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");
        PetItem petItem = createAndSavePetItem(pet, "사료", ItemCategory.FOOD);

        // when
        List<PetItemPurchaseResponse> result = petItemPurchaseReadService.getPurchaseHistory(petItem.getId());

        // then
        assertThat(result).isEmpty();
    }

    // ==================== PetItemPurchaseReadService - findLatestPurchaseDatesByPetItemIds ====================

    @DisplayName("여러 용품 ID에 대한 최근 구매일을 배치 조회한다.")
    @Test
    void findLatestPurchaseDatesByPetItemIds_success() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");
        PetItem petItem1 = createAndSavePetItem(pet, "사료", ItemCategory.FOOD);
        PetItem petItem2 = createAndSavePetItem(pet, "샴푸", ItemCategory.SHAMPOO);

        LocalDate latestDate1 = LocalDate.of(2024, 3, 10);
        petItemPurchaseJpaRepository.save(PetItemPurchase.of(petItem1, LocalDate.of(2024, 1, 5)));
        petItemPurchaseJpaRepository.save(PetItemPurchase.of(petItem1, latestDate1));

        LocalDate latestDate2 = LocalDate.of(2024, 2, 20);
        petItemPurchaseJpaRepository.save(PetItemPurchase.of(petItem2, latestDate2));

        List<Long> petItemIds = List.of(petItem1.getId(), petItem2.getId());

        // when
        Map<Long, LocalDate> result = petItemPurchaseReadService.findLatestPurchaseDatesByPetItemIds(petItemIds);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(petItem1.getId())).isEqualTo(latestDate1);
        assertThat(result.get(petItem2.getId())).isEqualTo(latestDate2);
    }

    // ==================== PetItemPurchaseReadService - findLatestPurchaseDateByPetItemId ====================

    @DisplayName("단일 용품의 가장 최근 구매일을 조회한다.")
    @Test
    void findLatestPurchaseDateByPetItemId_success() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");
        PetItem petItem = createAndSavePetItem(pet, "사료", ItemCategory.FOOD);

        petItemPurchaseJpaRepository.save(PetItemPurchase.of(petItem, LocalDate.of(2024, 1, 5)));
        LocalDate latestDate = LocalDate.of(2024, 3, 20);
        petItemPurchaseJpaRepository.save(PetItemPurchase.of(petItem, latestDate));

        // when
        Optional<LocalDate> result = petItemPurchaseReadService.findLatestPurchaseDateByPetItemId(petItem.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(latestDate);
    }

    @DisplayName("구매 이력이 없는 용품의 최근 구매일 조회 시 빈 Optional을 반환한다.")
    @Test
    void findLatestPurchaseDateByPetItemId_noPurchase() {
        // given
        User user = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        Pet pet = createAndSavePet(user, "초코");
        PetItem petItem = createAndSavePetItem(pet, "사료", ItemCategory.FOOD);

        // when
        Optional<LocalDate> result = petItemPurchaseReadService.findLatestPurchaseDateByPetItemId(petItem.getId());

        // then
        assertThat(result).isEmpty();
    }
}
