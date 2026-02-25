package com.puppynoteserver.pet.walk.controller;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.pet.walk.controller.request.WalkCreateRequest;
import com.puppynoteserver.pet.walk.service.WalkWriteService;
import com.puppynoteserver.pet.walk.service.response.WalkResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/walks")
public class WalkController {

    private final WalkWriteService walkWriteService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<WalkResponse> createWalk(@Valid @RequestBody WalkCreateRequest request) {
        return ApiResponse.created(walkWriteService.create(request.toServiceRequest()));
    }
}
