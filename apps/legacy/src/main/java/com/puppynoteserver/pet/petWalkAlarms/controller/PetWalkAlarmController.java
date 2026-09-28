package com.puppynoteserver.pet.petWalkAlarms.controller;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.pet.petWalkAlarms.controller.request.PetWalkAlarmCreateRequest;
import com.puppynoteserver.pet.petWalkAlarms.controller.request.PetWalkAlarmStatusUpdateRequest;
import com.puppynoteserver.pet.petWalkAlarms.controller.request.PetWalkAlarmUpdateRequest;
import com.puppynoteserver.pet.petWalkAlarms.service.PetWalkAlarmReadService;
import com.puppynoteserver.pet.petWalkAlarms.service.PetWalkAlarmWriteService;
import com.puppynoteserver.pet.petWalkAlarms.service.response.PetWalkAlarmResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pet-walk-alarms")
public class PetWalkAlarmController {

    private final PetWalkAlarmWriteService petWalkAlarmWriteService;
    private final PetWalkAlarmReadService petWalkAlarmReadService;

    @GetMapping
    public ApiResponse<List<PetWalkAlarmResponse>> getAlarms(@RequestParam Long petId) {
        return ApiResponse.ok(petWalkAlarmReadService.getAlarmsByPetId(petId));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<PetWalkAlarmResponse> createAlarm(@Valid @RequestBody PetWalkAlarmCreateRequest request) {
        return ApiResponse.created(petWalkAlarmWriteService.create(request.toServiceRequest()));
    }

    @PutMapping
    public ApiResponse<PetWalkAlarmResponse> updateAlarm(@Valid @RequestBody PetWalkAlarmUpdateRequest request) {
        return ApiResponse.ok(petWalkAlarmWriteService.update(request.toServiceRequest()));
    }

    @PatchMapping("/status")
    public ApiResponse<PetWalkAlarmResponse> updateAlarmStatus(@Valid @RequestBody PetWalkAlarmStatusUpdateRequest request) {
        return ApiResponse.ok(petWalkAlarmWriteService.updateStatus(request.toServiceRequest()));
    }

    @DeleteMapping("/{alarmId}")
    public ApiResponse<Void> deleteAlarm(@PathVariable Long alarmId) {
        petWalkAlarmWriteService.delete(alarmId);
        return ApiResponse.ok(null);
    }
}
