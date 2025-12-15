package com.enotes.controller;

import com.enotes.dto.*;
import com.enotes.entity.UserEntity;
import com.enotes.exceptions.UserNotFoundException;
import com.enotes.repo.UserDetailRepo;
import com.enotes.service.impl.UserDetailServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserDetailRepo userDetailRepo;

    @Autowired
    private UserDetailServiceImpl userDetailService;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegistrationDto dto) {

        UserEntity user = userDetailService.registerUser(dto);

        return ResponseEntity.ok(
                Map.of(
                        "message", "User registered successfully! Please check your email for verification link.",
                        "email", user.getEmail()
                )
        );
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(
            @RequestParam String email,
            @RequestParam String code
    ) {

        String response = userDetailService.verifyLink(email, code);

        return ResponseEntity.ok(
                Map.of(
                        "message", response,
                        "email", email
                )
        );
    }


    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(
            @Valid @RequestBody RegistrationDto dto) {

        Map<String, Object> response = userDetailService.registerAdmin(dto);

        return ResponseEntity.ok(
                Map.of(
                        "message", response.get("message"),
                        "email", ((UserEntity) response.get("user")).getEmail()
                )
        );
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        LoginResponse loginResponse = userDetailService.login(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "status", HttpStatus.OK.value(),
                        "message", "Login Successfully",
                        "data", loginResponse
                ));
    }

    @PostMapping("/forget-password")
    public ResponseEntity<?> forgetPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {

        userDetailService.forgetPassword(forgotPasswordRequest.getEmail());

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "status", HttpStatus.OK.value(),
                        "message", "Please Check Mail"
                ));
    }

    @PostMapping("/resetForgetPassword")
    public ResponseEntity<?> forgetPasswordReset(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {

        String response = userDetailService.forgetPasswordReset(resetPasswordRequest.getToken(),
                resetPasswordRequest.getNewPassword());

        return ResponseEntity.ok(
                Map.of(
                        "message", response,
                        "status", HttpStatus.OK.value()
                )
        );
    }


}
