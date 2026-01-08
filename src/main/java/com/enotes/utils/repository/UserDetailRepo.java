package com.enotes.utils.repository;

import com.enotes.utils.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDetailRepo extends JpaRepository<UserEntity, Integer> {

    //check mail
    Optional<UserEntity>findByEmail(String email);

    // check mobile No
    Optional<UserEntity>findByMobileNo(String mobileNo);

    Optional<UserEntity> findByVerificationCode(String code);

    Page<UserEntity> findByRoles_RoleName(String roleName, Pageable pageable);
}
