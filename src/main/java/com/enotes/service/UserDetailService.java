package com.enotes.service;

import com.enotes.dto.LoginRequest;
import com.enotes.dto.LoginResponse;
import com.enotes.dto.RegistrationDto;
import com.enotes.entity.UserEntity;

import java.util.Map;

public interface UserDetailService {

    //user registration only
    UserEntity registerUser(RegistrationDto dto);

    //verification link sending via mail
    String verifyLink(String email, String code);

    Map<String, Object> registerAdmin(RegistrationDto dto);

    LoginResponse login (LoginRequest loginRequest);
}
