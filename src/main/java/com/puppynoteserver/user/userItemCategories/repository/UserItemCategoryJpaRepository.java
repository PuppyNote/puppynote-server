package com.puppynoteserver.user.userItemCategories.repository;

import com.puppynoteserver.user.userItemCategories.entity.UserItemCategory;
import com.puppynoteserver.user.userItemCategories.entity.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserItemCategoryJpaRepository extends JpaRepository<UserItemCategory, Long> {

    List<UserItemCategory> findByUserIdAndCategoryTypeOrderBySort(Long userId, CategoryType categoryType);

    void deleteByUserIdAndCategoryType(Long userId, CategoryType categoryType);
}
