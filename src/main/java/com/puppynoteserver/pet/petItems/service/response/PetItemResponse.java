package com.puppynoteserver.pet.petItems.service.response;

import com.puppynoteserver.pet.petItems.entity.PetItem;
import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class PetItemResponse {

    private Long petItemId;
    private Long petId;
    private String name;
    private String majorCategory;
    private String majorCategoryName;
    private String majorCategoryEmoji;
    private ItemCategory category;
    private String categoryName;
    private String categoryEmoji;
    private int purchaseCycleDays;
    private String purchaseUrl;
    private String imageUrl;
    private LocalDate lastPurchasedAt;
    private LocalDate nextPurchaseAt;

    public static PetItemResponse of(PetItem petItem, String imageUrl, LocalDate lastPurchasedAt) {
        LocalDate nextPurchaseAt = lastPurchasedAt != null
                ? lastPurchasedAt.plusDays(petItem.getPurchaseCycleDays())
                : null;

        ItemCategory category = petItem.getCategory();

        return PetItemResponse.builder()
                .petItemId(petItem.getId())
                .petId(petItem.getPet().getId())
                .name(petItem.getName())
                .majorCategory(category.getMajorCategory().name())
                .majorCategoryName(category.getMajorCategory().getDescription())
                .majorCategoryEmoji(category.getMajorCategory().getEmoji())
                .category(category)
                .categoryName(category.getDescription())
                .categoryEmoji(category.getEmoji())
                .purchaseCycleDays(petItem.getPurchaseCycleDays())
                .purchaseUrl(petItem.getPurchaseUrl())
                .imageUrl(imageUrl)
                .lastPurchasedAt(lastPurchasedAt)
                .nextPurchaseAt(nextPurchaseAt)
                .build();
    }
}
