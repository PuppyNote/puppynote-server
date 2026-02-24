package com.puppynoteserver.user.users.controller;

import com.puppynoteserver.global.ApiResponse;
import com.puppynoteserver.user.users.controller.request.EmailSendRequest;
import com.puppynoteserver.user.users.controller.request.SignUpRequest;
import com.puppynoteserver.user.users.service.UserService;
import com.puppynoteserver.user.users.service.response.SignUpResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public ApiResponse<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        return ApiResponse.created(userService.signUp(request.toServiceRequest()));
    }

    @PostMapping("/email/send")
    public ApiResponse<String> sendVerificationEmail(@Valid @RequestBody EmailSendRequest request) {
        return ApiResponse.ok(userService.sendVerificationEmail(request.toServiceRequest()));
    }

}
