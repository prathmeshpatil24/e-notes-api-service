package com.enotes.security;


import com.enotes.utils.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    @Serial
    private static final long serialVersionUID= 1L;

    private UserEntity userEntity;

    public CustomUserDetails(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        List<SimpleGrantedAuthority> grantedAuthorities = userEntity.getRoles()
                .stream()
                .map(roleEntity ->
                        new SimpleGrantedAuthority(roleEntity.getRoleName())
                ).toList();

        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return userEntity.getIsActive();   // allow login only if active
    }

    public Integer getUserId() {
        return userEntity.getId();
    }

    //by default true setting
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
/*
We implement UserDetails to map our UserEntity into Spring Security's required format.
CustomUserDetails allows us to provide username, password, and roles,
which Spring Security uses for authentication and authorization.
Just a wrapper for UserEntity (POJO)
*/