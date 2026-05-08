package com.puppynoteserver.foodChat.service.response;

import lombok.Getter;

import java.util.List;

@Getter
public class FoodListResponse {

    private final List<FoodResponse> content;
    private final int page;
    private final long totalCount;

    private FoodListResponse(List<FoodResponse> content, int page, long totalCount) {
        this.content = content;
        this.page = page;
        this.totalCount = totalCount;
    }

    public static FoodListResponse of(List<FoodResponse> content, int page, long totalCount) {
        return new FoodListResponse(content, page, totalCount);
    }
}
