package com.puppynoteserver.user.user.adapter.in.web;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.user.user.adapter.in.web.request.EmailSendRequest;
import com.puppynoteserver.user.user.adapter.in.web.request.SignUpRequest;
import com.puppynoteserver.user.user.adapter.in.web.request.UserProfileUpdateRequest;
import com.puppynoteserver.user.user.application.port.in.UserFinder;
import com.puppynoteserver.user.user.application.port.in.UserRegister;
import com.puppynoteserver.user.user.application.port.in.UserRemover;
import com.puppynoteserver.user.user.application.port.in.UserUpdater;
import com.puppynoteserver.user.user.application.port.in.response.SignUpResponse;
import com.puppynoteserver.user.user.application.port.in.response.UserProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserRegister userRegister;
    private final UserFinder userFinder;
    private final UserUpdater userUpdater;
    private final UserRemover userRemover;


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public ApiResponse<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        return ApiResponse.created(userRegister.signUp(request.toServiceRequest()));
    }

    @PostMapping("/email/send")
    public ApiResponse<String> sendVerificationEmail(@Valid @RequestBody EmailSendRequest request) {
        return ApiResponse.ok(userService.sendVerificationEmail(request.toServiceRequest()));
    }

    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> getMyProfile() {
        return ApiResponse.ok(userFinder.getMyProfile());
    }

    @PatchMapping("/profile")
    public ApiResponse<Void> updateProfile(@Valid @RequestBody UserProfileUpdateRequest request) {
        userUpdater.updateProfile(request.toServiceRequest());
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/withdraw")
    public ApiResponse<Void> withdraw() {
        userRemover.withdraw();
        return ApiResponse.ok(null);
    }

}
