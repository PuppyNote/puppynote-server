package com.puppynoteserver.pet.userItemCategories.repository.impl;

import com.puppynoteserver.pet.userItemCategories.entity.UserItemCategory;
import com.puppynoteserver.pet.userItemCategories.repository.UserItemCategoryJpaRepository;
import com.puppynoteserver.pet.userItemCategories.repository.UserItemCategoryRepository;
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
    public List<UserItemCategory> findByUserIdOrderBySort(Long userId) {
        return userItemCategoryJpaRepository.findByUserIdOrderBySort(userId);
    }

    @Override
    public void deleteByUserId(Long userId) {
        userItemCategoryJpaRepository.deleteByUserId(userId);
    }
}
