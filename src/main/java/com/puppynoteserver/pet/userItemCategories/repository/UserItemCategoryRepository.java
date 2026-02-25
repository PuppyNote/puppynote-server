package com.puppynoteserver.pet.userItemCategories.repository;

import com.puppynoteserver.pet.userItemCategories.entity.UserItemCategory;

import java.util.List;

public interface UserItemCategoryRepository {

    void saveAll(List<UserItemCategory> userItemCategories);

    List<UserItemCategory> findByUserIdOrderBySort(Long userId);

    void deleteByUserId(Long userId);
}
