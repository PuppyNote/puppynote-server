package com.puppynoteserver.user.userItemCategories.repository.impl;

import com.puppynoteserver.user.userItemCategories.entity.UserItemCategory;
import com.puppynoteserver.user.userItemCategories.entity.enums.CategoryType;
import com.puppynoteserver.user.userItemCategories.repository.UserItemCategoryJpaRepository;
import com.puppynoteserver.user.userItemCategories.repository.UserItemCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserItemCategoryRepositoryImpl implements UserItemCategoryRepository {

    private final UserItemCategoryJpaRepository userItemCategoryJpaRepository;

    @Override
    public void saveAll(List<UserItemCategory> userItemCategories) {
        userItemCategoryJpaRepository.saveAll(userItemCategories);
    }

    @Override
    public List<UserItemCategory> findByUserIdAndCategoryTypeOrderBySort(Long userId, CategoryType categoryType) {
        return userItemCategoryJpaRepository.findByUserIdAndCategoryTypeOrderBySort(userId, categoryType);
    }

    @Override
    public void deleteByUserIdAndCategoryType(Long userId, CategoryType categoryType) {
        userItemCategoryJpaRepository.deleteByUserIdAndCategoryType(userId, categoryType);
    }
}
