package com.puppynoteserver.user.userItemCategories.service;

import com.puppynoteserver.user.userItemCategories.entity.enums.CategoryType;
import com.puppynoteserver.user.userItemCategories.service.response.UserItemCategoryResponse;

import java.util.List;

public interface UserItemCategoryReadService {

    List<UserItemCategoryResponse> getMyCategories(CategoryType categoryType);
}
