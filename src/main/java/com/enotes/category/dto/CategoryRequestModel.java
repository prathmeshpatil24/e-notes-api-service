package com.enotes.category.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequestModel {

    private String name;
    private String description;
    private Boolean isActive;

}
