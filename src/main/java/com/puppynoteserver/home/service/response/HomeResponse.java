package com.puppynoteserver.home.service.response;

import lombok.Getter;

@Getter
public class HomeResponse {

    private final String petName;
    private final String petProfileImageUrl;
    private final long recentWalkCount;
    private final long petItemCount;

    private HomeResponse(String petName, String petProfileImageUrl, long recentWalkCount, long petItemCount) {
        this.petName = petName;
        this.petProfileImageUrl = petProfileImageUrl;
        this.recentWalkCount = recentWalkCount;
        this.petItemCount = petItemCount;
    }

    public static HomeResponse of(String petName, String petProfileImageUrl, long recentWalkCount, long petItemCount) {
        return new HomeResponse(petName, petProfileImageUrl, recentWalkCount, petItemCount);
    }
}
