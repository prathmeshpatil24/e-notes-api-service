package com.enotes.security;

import com.enotes.entity.UserEntity;
import com.enotes.exceptions.UserNotFoundException;
import com.enotes.repo.UserDetailRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
UserDetailsService is used to load user data from the database during login.
Spring Security calls it automatically when the user tries to authenticate.
It fetches the user by username/email and
returns a UserDetails object that contains password and roles.

Loads user from DB for Spring Security
*/

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserDetailRepo userDetailRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity email = userDetailRepo.findByEmail(username)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with email:- " + username)
                );
        return new CustomUserDetails(email);
    }
}
