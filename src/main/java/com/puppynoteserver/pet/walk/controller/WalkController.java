package com.puppynoteserver.pet.walk.controller;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.pet.walk.controller.request.WalkCreateRequest;
import com.puppynoteserver.pet.walk.service.WalkReadService;
import com.puppynoteserver.pet.walk.service.WalkWriteService;
import com.puppynoteserver.pet.walk.service.response.WalkResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/walks")
public class WalkController {

    private final WalkWriteService walkWriteService;
    private final WalkReadService walkReadService;

    @GetMapping
    public ApiResponse<List<WalkResponse>> getWalks(
            @RequestParam Long petId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.ok(walkReadService.getWalksByPetId(petId, date));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<WalkResponse> createWalk(@Valid @RequestBody WalkCreateRequest request) {
        return ApiResponse.created(walkWriteService.create(request.toServiceRequest()));
    }
}
