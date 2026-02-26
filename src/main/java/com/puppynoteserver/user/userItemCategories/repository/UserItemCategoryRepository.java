package com.puppynoteserver.user.userItemCategories.repository;

import com.puppynoteserver.user.userItemCategories.entity.UserItemCategory;
import com.puppynoteserver.user.userItemCategories.entity.enums.CategoryType;

import java.util.List;

public interface UserItemCategoryRepository {

    void saveAll(List<UserItemCategory> userItemCategories);

    List<UserItemCategory> findByUserIdAndCategoryTypeOrderBySort(Long userId, CategoryType categoryType);

    void deleteByUserIdAndCategoryType(Long userId, CategoryType categoryType);
}
