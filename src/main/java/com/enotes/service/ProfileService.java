package com.enotes.service;

import com.enotes.dto.ChangePasswordRequest;
import com.enotes.entity.UserEntity;

public interface ProfileService {


    void changePassword(Integer userId, ChangePasswordRequest changePasswordRequest);
}
