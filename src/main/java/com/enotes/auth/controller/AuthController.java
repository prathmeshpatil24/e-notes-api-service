package com.enotes.auth.controller;

import com.enotes.auth.dto.*;
import com.enotes.auth.service.AuthServiceImpl;
import com.enotes.auth.dto.RegistrationDto;
import com.enotes.entity.UserEntity;
import com.enotes.repo.UserDetailRepo;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserDetailRepo userDetailRepo;

    private final AuthServiceImpl authService;

//    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegistrationDto dto) {

        UserEntity user = authService.registerUser(dto);

        return ResponseEntity.ok(
                Map.of(
                        "message", "User registered successfully! Please check your email for verification link.",
                        "email", user.getEmail(),
                        "timestamp", LocalDateTime.now()
                )
        );
    }

    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(
            @Valid @RequestBody RegistrationDto dto) {

        Map<String, Object> response = authService.registerAdmin(dto);

        return ResponseEntity.ok(
                Map.of(
                        "message", response.get("message"),
                        "email", ((UserEntity) response.get("user")).getEmail()
                )
        );
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(
            @RequestParam String email,
            @RequestParam String code
    ) {

        String response = authService.verifyLink(email, code);

        return ResponseEntity.ok(
                Map.of(
                        "message", response,
                        "email", email
                )
        );
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        LoginResponse loginResponse = authService.login(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "status", HttpStatus.OK.value(),
                        "message", "Login Successfully",
                        "data", loginResponse
                ));
    }

    @PostMapping("/forget-password")
    public ResponseEntity<?> forgetPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {

        authService.forgetPassword(forgotPasswordRequest.getEmail());

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "status", HttpStatus.OK.value(),
                        "message", "Please Check Mail"
                ));
    }

    @PostMapping("/resetForgetPassword")
    public ResponseEntity<?> forgetPasswordReset(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {

        String response = authService.forgetPasswordReset(resetPasswordRequest.getToken(),
                resetPasswordRequest.getNewPassword());

        return ResponseEntity.ok(
                Map.of(
                        "message", response,
                        "status", HttpStatus.OK.value()
                )
        );
    }

}
