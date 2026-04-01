package com.puppynoteserver.user.userItemCategories.service;

import com.puppynoteserver.IntegrationTestSupport;
import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;
import com.puppynoteserver.user.userItemCategories.entity.UserItemCategory;
import com.puppynoteserver.user.userItemCategories.entity.enums.CategoryType;
import com.puppynoteserver.user.userItemCategories.repository.UserItemCategoryJpaRepository;
import com.puppynoteserver.user.userItemCategories.service.response.UserItemCategoryResponse;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;

class UserItemCategoryServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserItemCategoryWriteService userItemCategoryWriteService;

    @Autowired
    private UserItemCategoryReadService userItemCategoryReadService;

    @Autowired
    private UserItemCategoryJpaRepository userItemCategoryJpaRepository;

    @AfterEach
    @Override
    public void tearDown() {
        userItemCategoryJpaRepository.deleteAllInBatch();
        super.tearDown();
    }

    @DisplayName("용품 카테고리를 정상적으로 저장한다.")
    @Test
    void 용품_카테고리를_정상적으로_저장한다() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        List<ItemCategory> categories = List.of(ItemCategory.FOOD, ItemCategory.SNACK);

        // when
        List<UserItemCategoryResponse> responses = userItemCategoryWriteService.saveCategories(CategoryType.ITEM, categories);

        // then
        assertThat(responses)
                .hasSize(2)
                .extracting("category", "sort")
                .containsExactly(
                        tuple(ItemCategory.FOOD.name(), 1),
                        tuple(ItemCategory.SNACK.name(), 2)
                );
    }

    @DisplayName("카테고리 저장 시 동일 타입의 기존 카테고리를 삭제하고 새로 저장한다.")
    @Test
    void 카테고리_저장_시_동일_타입의_기존_카테고리를_삭제하고_새로_저장한다() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        // 초기 저장
        userItemCategoryWriteService.saveCategories(CategoryType.ITEM, List.of(ItemCategory.FOOD, ItemCategory.SNACK));

        // when - 새로운 목록으로 교체
        List<UserItemCategoryResponse> responses = userItemCategoryWriteService.saveCategories(
                CategoryType.ITEM, List.of(ItemCategory.SHAMPOO));

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getCategory()).isEqualTo(ItemCategory.SHAMPOO.name());

        List<UserItemCategory> savedCategories = userItemCategoryJpaRepository
                .findByUserIdAndCategoryTypeOrderBySort(user.getId(), CategoryType.ITEM);
        assertThat(savedCategories).hasSize(1);
    }

    @DisplayName("다른 타입의 카테고리는 영향을 받지 않는다.")
    @Test
    void 다른_타입의_카테고리는_영향을_받지_않는다() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        // ITEM 타입 저장
        userItemCategoryWriteService.saveCategories(CategoryType.ITEM, List.of(ItemCategory.FOOD));

        // when - ACTIVITY 타입 저장
        userItemCategoryWriteService.saveCategories(CategoryType.ACTIVITY, List.of(ItemCategory.TOY));

        // then - ITEM 타입은 그대로 남아있어야 함
        List<UserItemCategory> itemCategories = userItemCategoryJpaRepository
                .findByUserIdAndCategoryTypeOrderBySort(user.getId(), CategoryType.ITEM);
        assertThat(itemCategories).hasSize(1);
        assertThat(itemCategories.get(0).getCategory()).isEqualTo(ItemCategory.FOOD);

        List<UserItemCategory> activityCategories = userItemCategoryJpaRepository
                .findByUserIdAndCategoryTypeOrderBySort(user.getId(), CategoryType.ACTIVITY);
        assertThat(activityCategories).hasSize(1);
        assertThat(activityCategories.get(0).getCategory()).isEqualTo(ItemCategory.TOY);
    }

    @DisplayName("내 카테고리 목록을 정상적으로 조회한다.")
    @Test
    void 내_카테고리_목록을_정상적으로_조회한다() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        userItemCategoryJpaRepository.saveAll(List.of(
                UserItemCategory.of(user, CategoryType.ITEM, ItemCategory.FOOD, 1),
                UserItemCategory.of(user, CategoryType.ITEM, ItemCategory.SNACK, 2)
        ));

        // when
        List<UserItemCategoryResponse> responses = userItemCategoryReadService.getMyCategories(CategoryType.ITEM);

        // then
        assertThat(responses)
                .hasSize(2)
                .extracting("sort")
                .containsExactly(1, 2);
    }

    @DisplayName("카테고리가 없을 때 빈 목록을 반환한다.")
    @Test
    void 카테고리가_없을_때_빈_목록을_반환한다() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        // when
        List<UserItemCategoryResponse> responses = userItemCategoryReadService.getMyCategories(CategoryType.ITEM);

        // then
        assertThat(responses).isEmpty();
    }

    @DisplayName("카테고리 타입별로 필터링하여 조회한다.")
    @Test
    void 카테고리_타입별로_필터링하여_조회한다() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        userItemCategoryJpaRepository.saveAll(List.of(
                UserItemCategory.of(user, CategoryType.ITEM, ItemCategory.FOOD, 1),
                UserItemCategory.of(user, CategoryType.ACTIVITY, ItemCategory.TOY, 1)
        ));

        // when
        List<UserItemCategoryResponse> itemResponses = userItemCategoryReadService.getMyCategories(CategoryType.ITEM);
        List<UserItemCategoryResponse> activityResponses = userItemCategoryReadService.getMyCategories(CategoryType.ACTIVITY);

        // then
        assertThat(itemResponses)
                .hasSize(1)
                .extracting("categoryType")
                .containsExactly(CategoryType.ITEM.name());

        assertThat(activityResponses)
                .hasSize(1)
                .extracting("categoryType")
                .containsExactly(CategoryType.ACTIVITY.name());
    }

    @DisplayName("빈 카테고리 목록으로 저장 시 기존 카테고리를 모두 삭제한다.")
    @Test
    void 빈_카테고리_목록으로_저장_시_기존_카테고리를_모두_삭제한다() {
        // given
        User user = userRepository.save(createUser("test@test.com", "password", SnsType.NORMAL));
        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        userItemCategoryWriteService.saveCategories(CategoryType.ITEM, List.of(ItemCategory.FOOD, ItemCategory.SNACK));

        // when
        List<UserItemCategoryResponse> responses = userItemCategoryWriteService.saveCategories(CategoryType.ITEM, List.of());

        // then
        assertThat(responses).isEmpty();

        List<UserItemCategory> savedCategories = userItemCategoryJpaRepository
                .findByUserIdAndCategoryTypeOrderBySort(user.getId(), CategoryType.ITEM);
        assertThat(savedCategories).isEmpty();
    }
}
