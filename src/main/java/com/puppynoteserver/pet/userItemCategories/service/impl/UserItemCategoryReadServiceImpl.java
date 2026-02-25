package com.puppynoteserver.pet.userItemCategories.service.impl;

import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.pet.userItemCategories.repository.UserItemCategoryRepository;
import com.puppynoteserver.pet.userItemCategories.service.UserItemCategoryReadService;
import com.puppynoteserver.pet.userItemCategories.service.response.UserItemCategoryResponse;
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
    public List<UserItemCategoryResponse> getMyCategories() {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();

        return userItemCategoryRepository.findByUserIdOrderBySort(userId).stream()
                .map(UserItemCategoryResponse::from)
                .toList();
    }
}
