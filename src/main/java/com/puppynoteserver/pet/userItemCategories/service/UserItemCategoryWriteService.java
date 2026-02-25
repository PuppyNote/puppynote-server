package com.puppynoteserver.pet.userItemCategories.service;

import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;
import com.puppynoteserver.pet.userItemCategories.service.response.UserItemCategoryResponse;

import java.util.List;

public interface UserItemCategoryWriteService {

    List<UserItemCategoryResponse> saveCategories(List<ItemCategory> categories);
}
