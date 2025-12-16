package com.enotes.service.impl;

import com.enotes.dto.ChangePasswordRequest;
import com.enotes.entity.UserEntity;
import com.enotes.repo.UserDetailRepo;
import com.enotes.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserDetailRepo userDetailRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void changePassword(Integer userId, ChangePasswordRequest request) {

        UserEntity user = userDetailRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(
                request.getOldPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException("Old password is incorrect");
        }

       try {
           user.setPassword(passwordEncoder.encode(request.getNewPassword()));
           userDetailRepo.save(user);
       } catch (Exception e) {
           e.printStackTrace();
           throw new RuntimeException(e);
       }
    }

}
