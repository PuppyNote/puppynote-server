package com.puppynoteserver.pet.petItems.service.response;

import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;
import com.puppynoteserver.pet.petItems.entity.enums.MajorCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ItemCategoryGroupResponse {

    private String majorCategory;
    private String majorCategoryName;
    private String majorCategoryEmoji;
    private List<ItemCategoryInfo> categories;

    @Getter
    @Builder
    public static class ItemCategoryInfo {
        private String category;
        private String categoryName;
        private String emoji;
    }

    public static List<ItemCategoryGroupResponse> ofAll() {
        return Arrays.stream(MajorCategory.values())
                .map(major -> {
                    List<ItemCategoryInfo> items = Arrays.stream(ItemCategory.values())
                            .filter(cat -> cat.getMajorCategory() == major)
                            .map(cat -> ItemCategoryInfo.builder()
                                    .category(cat.name())
                                    .categoryName(cat.getDescription())
                                    .emoji(cat.getEmoji())
                                    .build())
                            .collect(Collectors.toList());

                    return ItemCategoryGroupResponse.builder()
                            .majorCategory(major.name())
                            .majorCategoryName(major.getDescription())
                            .majorCategoryEmoji(major.getEmoji())
                            .categories(items)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
