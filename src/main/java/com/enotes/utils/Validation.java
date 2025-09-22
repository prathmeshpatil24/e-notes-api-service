package com.enotes.utils;

import com.enotes.dto.CategoryRequestModel;
import com.enotes.exceptions.ValidationException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class Validation {

    public void categoryValidation(CategoryRequestModel categoryRequestModel) {

        Map<String, Object> error = new LinkedHashMap<>();

        if (ObjectUtils.isEmpty(categoryRequestModel)) {
            throw new IllegalArgumentException("category Object/JSON shouldn't be null or empty");
        } else {

            // validation name field
            if (ObjectUtils.isEmpty(categoryRequestModel.getName())) {
                error.put("name", "name field is empty or null");
            } else {
                if (categoryRequestModel.getName().length() < 5) {
                    error.put("name", "name length min 10");
                }
                if (categoryRequestModel.getName().length() > 100) {
                    error.put("name", "name length max 100");
                }
            }

            // validation dscription
            if (ObjectUtils.isEmpty(categoryRequestModel.getDescription())) {
                error.put("description", "description field is empty or null");
            }else {
                if (categoryRequestModel.getDescription().length() < 10) {
                    error.put("description", "description length min 10");
                }
                if (categoryRequestModel.getName().length() > 100) {
                    error.put("description", "description length max 100");
                }
            }

            // validation isActive
            if (ObjectUtils.isEmpty(categoryRequestModel.getIsActive())) {
                error.put("isActive", "isActive field is empty or null, it should be true or false");
            } else {
                if (categoryRequestModel.getIsActive() != Boolean.TRUE.booleanValue()
                        && categoryRequestModel.getIsActive() != Boolean.FALSE.booleanValue()) {
                    error.put("isActive", "invalid value isActive field, it should be true or false");
                }
            }
        }

        if (!error.isEmpty()) {
            throw new ValidationException(error);
        }

    }



}
