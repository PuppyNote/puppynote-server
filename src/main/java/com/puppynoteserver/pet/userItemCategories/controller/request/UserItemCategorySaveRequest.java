package com.puppynoteserver.pet.userItemCategories.controller.request;

import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class UserItemCategorySaveRequest {

    @NotNull(message = "카테고리 목록은 필수입니다.")
    private List<ItemCategory> categories;
}
