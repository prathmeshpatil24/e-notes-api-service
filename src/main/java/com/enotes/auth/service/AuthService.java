package com.enotes.auth.service;

import com.enotes.auth.dto.LoginRequest;
import com.enotes.auth.dto.LoginResponse;
import com.enotes.auth.dto.RegistrationDto;
import com.enotes.utils.entity.UserEntity;

import java.util.Map;

public interface AuthService {

    //user registration only
    UserEntity registerUser(RegistrationDto dto);

    //admin registeration only
    Map<String, Object> registerAdmin(RegistrationDto dto);

    //verification link/password reset sending via mail
    String verifyLink(String email, String code);

    //login for both user and admin
    LoginResponse login (LoginRequest loginRequest);

    //forget pwd for both with mail checking
    void forgetPassword(String email);

    //reset pwd for both
    String forgetPasswordReset(String code, String newPassword);
}
