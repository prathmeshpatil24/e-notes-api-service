package com.enotes.repo;

import com.enotes.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDetailRepo extends JpaRepository<UserEntity, Integer> {

    //check mail
    Optional<UserEntity>findByEmail(String email);

    // check mobile No
    Optional<UserEntity>findByMobileNo(String mobileNo);

    Optional<UserEntity> findByVerificationCode(String code);

}
