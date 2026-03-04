package com.puppynoteserver.pet.familyMembers.controller;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.pet.familyMembers.controller.request.FamilyMemberInviteRequest;
import com.puppynoteserver.pet.familyMembers.controller.request.FamilyMemberRegisterRequest;
import com.puppynoteserver.pet.familyMembers.service.FamilyMemberReadService;
import com.puppynoteserver.pet.familyMembers.service.FamilyMemberWriteService;
import com.puppynoteserver.pet.familyMembers.service.response.FamilyMemberResponse;
import com.puppynoteserver.pet.familyMembers.service.response.UserSearchResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/family-members")
public class FamilyMemberController {

    private final FamilyMemberReadService familyMemberReadService;
    private final FamilyMemberWriteService familyMemberWriteService;

    @GetMapping
    public ApiResponse<List<FamilyMemberResponse>> getFamilyMembers() {
        return ApiResponse.ok(familyMemberReadService.getFamilyMembers());
    }

    @GetMapping("/search")
    public ApiResponse<List<UserSearchResponse>> searchUsers(@RequestParam String email) {
        return ApiResponse.ok(familyMemberReadService.searchUsersByEmail(email));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/invite")
    public ApiResponse<Void> invite(@Valid @RequestBody FamilyMemberInviteRequest request) {
        familyMemberWriteService.invite(request.toServiceRequest());
        return ApiResponse.created(null);
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody FamilyMemberRegisterRequest request) {
        familyMemberWriteService.register(request.toServiceRequest());
        return ApiResponse.ok(null);
    }
}
