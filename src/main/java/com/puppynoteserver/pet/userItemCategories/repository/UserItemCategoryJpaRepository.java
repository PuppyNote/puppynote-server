package com.puppynoteserver.pet.userItemCategories.repository;

import com.puppynoteserver.pet.userItemCategories.entity.UserItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserItemCategoryJpaRepository extends JpaRepository<UserItemCategory, Long> {

    List<UserItemCategory> findByUserIdOrderBySort(Long userId);

    void deleteByUserId(Long userId);
}
