package com.puppynoteserver.home.service.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
public class HomeResponse {

    private final String petName;
    private final String petProfileImageUrl;
    private final String petAge;
    private final Integer birthdayDday;
    private final boolean walkedToday;
    private final Integer daysSinceLastWalk;
    private final long monthlyWalkMinutes;
    private final long recentWalkCount;
    private final long petItemCount;
    private final List<LocalTime> todayWalkAlarmTimes;

    @Builder
    private HomeResponse(String petName, String petProfileImageUrl, String petAge, Integer birthdayDday,
                         boolean walkedToday, Integer daysSinceLastWalk, long monthlyWalkMinutes,
                         long recentWalkCount, long petItemCount,
                         List<LocalTime> todayWalkAlarmTimes) {
        this.petName = petName;
        this.petProfileImageUrl = petProfileImageUrl;
        this.petAge = petAge;
        this.birthdayDday = birthdayDday;
        this.walkedToday = walkedToday;
        this.daysSinceLastWalk = daysSinceLastWalk;
        this.monthlyWalkMinutes = monthlyWalkMinutes;
        this.recentWalkCount = recentWalkCount;
        this.petItemCount = petItemCount;
        this.todayWalkAlarmTimes = todayWalkAlarmTimes;
    }
}
