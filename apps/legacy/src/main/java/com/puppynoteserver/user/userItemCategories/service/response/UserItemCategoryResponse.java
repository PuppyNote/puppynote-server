package com.puppynoteserver.user.userItemCategories.service.response;

import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;
import com.puppynoteserver.pet.petItems.entity.enums.MajorCategory;
import com.puppynoteserver.user.userItemCategories.entity.UserItemCategory;
import com.puppynoteserver.user.userItemCategories.entity.enums.CategoryType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserItemCategoryResponse {

    private Long userItemCategoryId;
    private String categoryType;
    private String categoryTypeDescription;
    private String majorCategory;
    private String majorCategoryName;
    private String majorCategoryEmoji;
    private String category;
    private String categoryName;
    private String categoryEmoji;
    private int sort;

    public static UserItemCategoryResponse from(UserItemCategory entity) {
        ItemCategory category = entity.getCategory();
        MajorCategory major = category.getMajorCategory();
        CategoryType categoryType = entity.getCategoryType();

        return UserItemCategoryResponse.builder()
                .userItemCategoryId(entity.getId())
                .categoryType(categoryType.name())
                .categoryTypeDescription(categoryType.getDescription())
                .majorCategory(major.name())
                .majorCategoryName(major.getDescription())
                .majorCategoryEmoji(major.getEmoji())
                .category(category.name())
                .categoryName(category.getDescription())
                .categoryEmoji(category.getEmoji())
                .sort(entity.getSort())
                .build();
    }
}
