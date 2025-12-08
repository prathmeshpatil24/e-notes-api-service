package com.enotes.service;

import com.enotes.dto.RegistrationDto;
import com.enotes.entity.UserEntity;

public interface UserDetailService {

    UserEntity registerUser(RegistrationDto dto);

    String verifyLink(String email, String code);

}
