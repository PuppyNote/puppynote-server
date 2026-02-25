package com.puppynoteserver.pet.userItemCategories.service.impl;

import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;
import com.puppynoteserver.pet.userItemCategories.entity.UserItemCategory;
import com.puppynoteserver.pet.userItemCategories.repository.UserItemCategoryRepository;
import com.puppynoteserver.pet.userItemCategories.service.UserItemCategoryWriteService;
import com.puppynoteserver.pet.userItemCategories.service.response.UserItemCategoryResponse;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.service.UserReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserItemCategoryWriteServiceImpl implements UserItemCategoryWriteService {

    private final UserItemCategoryRepository userItemCategoryRepository;
    private final SecurityService securityService;
    private final UserReadService userReadService;

    @Override
    public List<UserItemCategoryResponse> saveCategories(List<ItemCategory> categories) {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        User user = userReadService.findById(userId);

        // 기존 카테고리 전체 삭제 후 새로 저장 (bulk replace)
        userItemCategoryRepository.deleteByUserId(userId);

        List<UserItemCategory> entities = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            entities.add(UserItemCategory.of(user, categories.get(i), i + 1));
        }

        userItemCategoryRepository.saveAll(entities);

        return entities.stream()
                .map(UserItemCategoryResponse::from)
                .toList();
    }
}
