package com.enotes.admin.dto;

import com.enotes.utils.entity.RoleEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserData {

    private Integer id;

    private String firstName;

    private String lastName;

    private String email;

    private String mobileNo;

    private Boolean isActive;

    private Set<RoleEntity> roles;
}
