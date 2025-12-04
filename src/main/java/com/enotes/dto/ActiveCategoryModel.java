package com.enotes.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActiveCategoryModel {

    private Integer id;
    private String name;
    private String description;
    private Boolean isActive;

}
