package com.puppynoteserver.user.userItemCategories.repository;

import com.puppynoteserver.user.userItemCategories.entity.UserItemCategory;
import com.puppynoteserver.user.userItemCategories.entity.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserItemCategoryJpaRepository extends JpaRepository<UserItemCategory, Long> {

    List<UserItemCategory> findByUserIdAndCategoryTypeOrderBySort(Long userId, CategoryType categoryType);

    @Modifying
    @Query("DELETE FROM UserItemCategory uic WHERE uic.user.id = :userId AND uic.categoryType = :categoryType")
    void deleteByUserIdAndCategoryType(@Param("userId") Long userId, @Param("categoryType") CategoryType categoryType);
}
