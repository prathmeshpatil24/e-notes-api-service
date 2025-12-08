package com.enotes.service.impl;

import com.enotes.dto.RegistrationDto;
import com.enotes.entity.RoleEntity;
import com.enotes.entity.UserEntity;
import com.enotes.repo.RoleRepo;
import com.enotes.repo.UserDetailRepo;
import com.enotes.service.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailService {

    @Autowired
    private UserDetailRepo userDetailRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Override
    public UserEntity registerUser(RegistrationDto dto) {

        if(userDetailRepo.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("user with this email is already present");
        }

        if(userDetailRepo.findByMobileNo(dto.getMobileNo()).isPresent()) {
            throw new RuntimeException("user with this mobileNo is already present");
        }


        // Create new user entity
        UserEntity user = new UserEntity();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setMobileNo(dto.getMobileNo());
        user.setPassword(dto.getPassword());
//        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        user.setIsActive(true);

        // Assign default role
        RoleEntity roleUser = roleRepo.findById(1)
                .orElseThrow(() -> new RuntimeException("Role USER not found"));
        user.getRoles().add(roleUser);

        return userDetailRepo.save(user);
    }
}
