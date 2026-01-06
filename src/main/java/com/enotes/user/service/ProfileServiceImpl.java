package com.enotes.user.service;

import com.enotes.service.impl.TokenBlockServiceImpl;
import com.enotes.user.dto.ChangePasswordRequest;
import com.enotes.entity.UserEntity;
import com.enotes.repo.UserDetailRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserDetailRepo userDetailRepo;

    @Autowired
    private TokenBlockServiceImpl tokenBlockService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserEntity profileInfo(Integer userId) {
        UserEntity userEntity = userDetailRepo.findById(userId).get();
        return userEntity;
    }

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

    @Override
    public void logout(String token) {
        tokenBlockService.blockToken(token);
    }

}
