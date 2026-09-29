package com.puppynoteserver.pet.walk.application.port.in;

import com.puppynoteserver.pet.walk.application.port.in.response.WalkCalendarResponse;
import com.puppynoteserver.pet.walk.application.port.in.response.WalkDetailResponse;
import com.puppynoteserver.pet.walk.application.port.in.response.WalkResponse;
import com.puppynoteserver.pet.walk.domain.entity.Walk;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface WalkFinder {

    Walk findById(Long walkId);

    List<WalkResponse> getWalksByPetId(Long petId, LocalDate date);

    List<WalkCalendarResponse> getWalkCalendar(Long petId, YearMonth yearMonth);

    WalkDetailResponse getWalkDetail(Long walkId);

    long countRecentWalks(Long petId, LocalDate from, LocalDate to);

    boolean walkedToday(Long petId);

    Integer daysSinceLastWalk(Long petId);

    long monthlyWalkMinutes(Long petId);
}
