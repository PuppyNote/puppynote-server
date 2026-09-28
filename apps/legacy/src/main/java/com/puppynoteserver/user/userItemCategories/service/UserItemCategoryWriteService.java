package com.puppynoteserver.user.userItemCategories.service;

import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;
import com.puppynoteserver.user.userItemCategories.entity.enums.CategoryType;
import com.puppynoteserver.user.userItemCategories.service.response.UserItemCategoryResponse;

import java.util.List;

public interface UserItemCategoryWriteService {

    List<UserItemCategoryResponse> saveCategories(CategoryType categoryType, List<ItemCategory> categories);
}
