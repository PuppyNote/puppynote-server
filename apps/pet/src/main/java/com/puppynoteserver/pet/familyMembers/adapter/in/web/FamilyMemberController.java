package com.puppynoteserver.pet.familyMembers.adapter.in.web;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.pet.familyMembers.adapter.in.web.request.FamilyMemberInviteRequest;
import com.puppynoteserver.pet.familyMembers.adapter.in.web.request.FamilyMemberRegisterRequest;
import com.puppynoteserver.pet.familyMembers.application.port.in.FamilyMemberFinder;
import com.puppynoteserver.pet.familyMembers.application.port.in.FamilyMemberRegister;
import com.puppynoteserver.pet.familyMembers.application.port.in.FamilyMemberRemover;
import com.puppynoteserver.pet.familyMembers.application.port.in.response.FamilyMemberResponse;
import com.puppynoteserver.pet.familyMembers.application.port.in.response.UserSearchResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/family-members")
public class FamilyMemberController {

    private final FamilyMemberFinder familyMemberFinder;
    private final FamilyMemberRegister familyMemberRegister;
    private final FamilyMemberRemover familyMemberRemover;

    @GetMapping
    public ApiResponse<List<FamilyMemberResponse>> getFamilyMembers(@RequestParam Long petId) {
        return ApiResponse.ok(familyMemberFinder.getFamilyMembers(petId));
    }

    @GetMapping("/search")
    public ApiResponse<List<UserSearchResponse>> searchUsers(@RequestParam String email) {
        return ApiResponse.ok(familyMemberFinder.searchUsersByEmail(email));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/invite")
    public ApiResponse<Void> invite(@Valid @RequestBody FamilyMemberInviteRequest request) {
        familyMemberRegister.invite(request.toServiceRequest());
        return ApiResponse.created(null);
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody FamilyMemberRegisterRequest request) {
        familyMemberRegister.register(request.toServiceRequest());
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{targetUserId}")
    public ApiResponse<Void> deleteFamilyRelation(@PathVariable Long targetUserId, @RequestParam Long petId) {
        familyMemberRemover.deleteFamilyRelation(targetUserId, petId);
        return ApiResponse.ok(null);
    }
}
