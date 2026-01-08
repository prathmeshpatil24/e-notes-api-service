package com.enotes.user.controller;

import com.enotes.audit.AuditAwareConfig;
import com.enotes.utils.entity.UserEntity;
import com.enotes.user.dto.ChangePasswordRequest;
import com.enotes.exceptions.UserNotFoundException;
import com.enotes.user.service.ProfileServiceImpl;
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

    //in user profile show all info of user for notes to-do also

    @GetMapping("/home")
    public String profile(Principal principal){
        return principal.getName() + " Welcome....!";
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

    @GetMapping("/profile")
    public ResponseEntity<?> profileDetails(){
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"
                ));
        UserEntity userEntity = profileService.profileInfo(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "status", HttpStatus.OK.value(),
                        "data", userEntity
                ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message", "Authorization header missing or invalid"
                    )
            );
        }

        System.out.println(authHeader);
        String token = authHeader.substring(7); // remove "Bearer "

        profileService.logout(token);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Logged out successfully"
                )
        );
    }
}
