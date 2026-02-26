package com.puppynoteserver.pet.userItemCategories.service;

import com.puppynoteserver.pet.userItemCategories.entity.enums.CategoryType;
import com.puppynoteserver.pet.userItemCategories.service.response.UserItemCategoryResponse;

import java.util.List;

public interface UserItemCategoryReadService {

    List<UserItemCategoryResponse> getMyCategories(CategoryType categoryType);
}
