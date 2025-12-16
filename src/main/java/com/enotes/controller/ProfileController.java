package com.enotes.controller;

import com.enotes.cofig.AuditAwareConfig;
import com.enotes.dto.ChangePasswordRequest;
import com.enotes.exceptions.UserNotFoundException;
import com.enotes.service.impl.ProfileServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProfileController {

    @Autowired
    private ProfileServiceImpl profileService;

    @Autowired
    private AuditAwareConfig auditAwareConfig;

    @GetMapping("/home")
    public String profile(Principal principal){
        return principal.getName() + "Welcome....!";
    }

    @PostMapping("change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request){

        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"
                ));

        profileService.changePassword(userId,request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "status", HttpStatus.OK.value(),
                        "message", "password updated successfully for userId:- " + userId
                ));

    }
}
