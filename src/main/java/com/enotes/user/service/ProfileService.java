package com.enotes.user.service;

import com.enotes.entity.UserEntity;
import com.enotes.user.dto.ChangePasswordRequest;

public interface ProfileService {

    UserEntity profileInfo(Integer userId);


    void changePassword(Integer userId, ChangePasswordRequest changePasswordRequest);

    void logout(String token);
}
