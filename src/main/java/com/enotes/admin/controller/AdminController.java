package com.enotes.admin.controller;


import com.enotes.admin.dto.UserData;
import com.enotes.admin.service.AdminServiceImpl;
import com.enotes.audit.AuditAwareConfig;
import com.enotes.dto.PaginationResponse;
import com.enotes.exceptions.UserNotFoundException;
import lombok.AllArgsConstructor;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminServiceImpl adminService;

    private final AuditAwareConfig auditAwareConfig;


    @GetMapping("/all-users")
    public ResponseEntity<?>getAllUSers(  @RequestParam(defaultValue = "0") Integer pageNo,
                                          @RequestParam(defaultValue = "5") Integer pageSize,
                                          @RequestParam(defaultValue = "createdAt") String sortBy,
                                          @RequestParam(defaultValue = "asc") String sortDir){

        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"
                ));

        PaginationResponse<UserData> userData = adminService.listOfUsers(pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "data", userData,
                        "message", "data fetched successfully",
                        "timestamp", LocalDateTime.now()
                ));

    }


}
