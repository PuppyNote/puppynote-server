package com.puppynoteserver.pet.petWalkAlarms.adapter.in.web;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.pet.petWalkAlarms.adapter.in.web.request.PetWalkAlarmCreateRequest;
import com.puppynoteserver.pet.petWalkAlarms.adapter.in.web.request.PetWalkAlarmStatusUpdateRequest;
import com.puppynoteserver.pet.petWalkAlarms.adapter.in.web.request.PetWalkAlarmUpdateRequest;
import com.puppynoteserver.pet.petWalkAlarms.application.port.in.PetWalkAlarmFinder;
import com.puppynoteserver.pet.petWalkAlarms.application.port.in.PetWalkAlarmWriter;
import com.puppynoteserver.pet.petWalkAlarms.application.port.in.response.PetWalkAlarmResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pet-walk-alarms")
public class PetWalkAlarmController {

    private final PetWalkAlarmWriter petWalkAlarmWriter;
    private final PetWalkAlarmFinder petWalkAlarmFinder;

    @GetMapping
    public ApiResponse<List<PetWalkAlarmResponse>> getAlarms(@RequestParam Long petId) {
        return ApiResponse.ok(petWalkAlarmFinder.getAlarmsByPetId(petId));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<PetWalkAlarmResponse> createAlarm(@Valid @RequestBody PetWalkAlarmCreateRequest request) {
        return ApiResponse.created(petWalkAlarmWriter.create(request.toServiceRequest()));
    }

    @PutMapping
    public ApiResponse<PetWalkAlarmResponse> updateAlarm(@Valid @RequestBody PetWalkAlarmUpdateRequest request) {
        return ApiResponse.ok(petWalkAlarmWriter.update(request.toServiceRequest()));
    }

    @PatchMapping("/status")
    public ApiResponse<PetWalkAlarmResponse> updateAlarmStatus(@Valid @RequestBody PetWalkAlarmStatusUpdateRequest request) {
        return ApiResponse.ok(petWalkAlarmWriter.updateStatus(request.toServiceRequest()));
    }

    @DeleteMapping("/{alarmId}")
    public ApiResponse<Void> deleteAlarm(@PathVariable Long alarmId) {
        petWalkAlarmWriter.delete(alarmId);
        return ApiResponse.ok(null);
    }
}
