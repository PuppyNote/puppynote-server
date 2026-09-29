package com.puppynoteserver.pet.walk.adapter.in.web;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.pet.walk.adapter.in.web.request.WalkCreateRequest;
import com.puppynoteserver.pet.walk.application.port.in.WalkFinder;
import com.puppynoteserver.pet.walk.application.port.in.WalkRegister;
import com.puppynoteserver.pet.walk.application.port.in.WalkRemover;
import com.puppynoteserver.pet.walk.application.port.in.response.WalkCalendarResponse;
import com.puppynoteserver.pet.walk.application.port.in.response.WalkDetailResponse;
import com.puppynoteserver.pet.walk.application.port.in.response.WalkResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/walks")
public class WalkController {

    private final WalkRegister walkRegister;
    private final WalkFinder walkFinder;
    private final WalkRemover walkRemover;

    @GetMapping("/{walkId}")
    public ApiResponse<WalkDetailResponse> getWalkDetail(@PathVariable Long walkId) {
        return ApiResponse.ok(walkFinder.getWalkDetail(walkId));
    }

    @GetMapping("/calendar")
    public ApiResponse<List<WalkCalendarResponse>> getWalkCalendar(
            @RequestParam Long petId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        return ApiResponse.ok(walkFinder.getWalkCalendar(petId, yearMonth));
    }

    @GetMapping
    public ApiResponse<List<WalkResponse>> getWalks(
            @RequestParam Long petId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.ok(walkFinder.getWalksByPetId(petId, date));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<WalkResponse> createWalk(@Valid @RequestBody WalkCreateRequest request) {
        return ApiResponse.created(walkRegister.create(request.toServiceRequest()));
    }

    @DeleteMapping("/{walkId}")
    public ApiResponse<Void> deleteWalk(@PathVariable Long walkId) {
        walkRemover.delete(walkId);
        return ApiResponse.ok(null);
    }
}
