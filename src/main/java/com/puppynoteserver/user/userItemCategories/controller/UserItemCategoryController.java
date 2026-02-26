package com.puppynoteserver.user.userItemCategories.controller;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.user.userItemCategories.controller.request.UserItemCategorySaveRequest;
import com.puppynoteserver.user.userItemCategories.entity.enums.CategoryType;
import com.puppynoteserver.user.userItemCategories.service.UserItemCategoryReadService;
import com.puppynoteserver.user.userItemCategories.service.UserItemCategoryWriteService;
import com.puppynoteserver.user.userItemCategories.service.response.UserItemCategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-item-categories")
public class UserItemCategoryController {

    private final UserItemCategoryWriteService userItemCategoryWriteService;
    private final UserItemCategoryReadService userItemCategoryReadService;

    @PostMapping
    public ApiResponse<List<UserItemCategoryResponse>> saveCategories(
            @Valid @RequestBody UserItemCategorySaveRequest request) {
        return ApiResponse.ok(
                userItemCategoryWriteService.saveCategories(request.getCategoryType(), request.getCategories())
        );
    }

    @GetMapping
    public ApiResponse<List<UserItemCategoryResponse>> getMyCategories(
            @RequestParam CategoryType categoryType) {
        return ApiResponse.ok(userItemCategoryReadService.getMyCategories(categoryType));
    }
}
