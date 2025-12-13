package com.enotes.auth.controller;

import com.enotes.dto.RegistrationDto;
import com.enotes.entity.UserEntity;
import com.enotes.service.impl.UserDetailServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    @Autowired
    private UserDetailServiceImpl userDetailService;
    
    
    @PostMapping("/register")
    public ResponseEntity<?>registerUser(
            @Valid @RequestBody RegistrationDto dto){

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
    public ResponseEntity<?>registerAdmin(
            @Valid @RequestBody RegistrationDto dto){

        Map<String, Object> response = userDetailService.registerAdmin(dto);

        return ResponseEntity.ok(
                Map.of(
                        "message", response.get("message"),
                        "email", ((UserEntity) response.get("user")).getEmail()
                )
        );
    }

}
