package com.puppynoteserver.pet.walk.service.response;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class WalkCalendarResponse {

    private final LocalDate date;
    private final boolean hasWalk;

    private WalkCalendarResponse(LocalDate date, boolean hasWalk) {
        this.date = date;
        this.hasWalk = hasWalk;
    }

    public static WalkCalendarResponse of(LocalDate date, boolean hasWalk) {
        return new WalkCalendarResponse(date, hasWalk);
    }
}
