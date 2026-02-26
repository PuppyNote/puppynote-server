package com.puppynoteserver.user.userItemCategories.service.impl;

import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.user.userItemCategories.entity.enums.CategoryType;
import com.puppynoteserver.user.userItemCategories.repository.UserItemCategoryRepository;
import com.puppynoteserver.user.userItemCategories.service.UserItemCategoryReadService;
import com.puppynoteserver.user.userItemCategories.service.response.UserItemCategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserItemCategoryReadServiceImpl implements UserItemCategoryReadService {

    private final UserItemCategoryRepository userItemCategoryRepository;
    private final SecurityService securityService;

    @Override
    public List<UserItemCategoryResponse> getMyCategories(CategoryType categoryType) {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();

        return userItemCategoryRepository.findByUserIdAndCategoryTypeOrderBySort(userId, categoryType).stream()
                .map(UserItemCategoryResponse::from)
                .toList();
    }
}
