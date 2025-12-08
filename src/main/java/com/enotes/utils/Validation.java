package com.enotes.utils;

import com.enotes.dto.CategoryRequestModel;
import com.enotes.dto.NotesRequestModel;
import com.enotes.dto.TodoRequest;
import com.enotes.enums.TodoStatus;
import com.enotes.exceptions.ValidationException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class Validation {

    //category validation
    public void categoryValidation(CategoryRequestModel categoryRequestModel) {

        Map<String, Object> error = new LinkedHashMap<>();

        if (ObjectUtils.isEmpty(categoryRequestModel)) {
            throw new IllegalArgumentException("category Object/JSON shouldn't be null or empty");
        } else {

            // validation name field
            if (ObjectUtils.isEmpty(categoryRequestModel.getName())) {
                error.put("name", "name field is empty or null");
            } else {
                String name = categoryRequestModel.getName();

                if (name.length() < 2) {
                    error.put("name", "name length must be at least 2 characters");
                } else if (name.length() > 100) {
                    error.put("name", "name length must not exceed 100 characters");
                }
            }

            // validation dscription
            if (ObjectUtils.isEmpty(categoryRequestModel.getDescription())) {
                error.put("description", "description field is empty or null");
            }else {
                if (categoryRequestModel.getDescription().length() < 10) {
                    error.put("description", "description length min 10");
                }
                if (categoryRequestModel.getDescription().length() > 500) {
                    error.put("description", "description length max 500");
                }
            }

            // validation isActive
                Boolean isActive = categoryRequestModel.getIsActive();
                if (isActive == null) {
                    error.put("isActive", "invalid value isActive field, it should be true or false");
                }
        }

        if (!error.isEmpty()) {
            throw new ValidationException(error);
        }

    }

    // notes validation
    public void notesValidation(NotesRequestModel notesRequestModel) {
        Map<String, Object> error = new LinkedHashMap<>();

        // Validate request object
        if (ObjectUtils.isEmpty(notesRequestModel)) {
            throw new IllegalArgumentException("Notes Object/JSON shouldn't be null or empty");
        }

        // Validate title
        if (ObjectUtils.isEmpty(notesRequestModel.getNoteTitle())) {
            error.put("title", "Title is required");
        } else {
            if (notesRequestModel.getNoteTitle().length() < 2) {
                error.put("title", "Title length min 2 characters");
            }
            if (notesRequestModel.getNoteTitle().length() > 500) {
                error.put("title", "Title length max 500 characters");
            }
        }

        // Validate description
        if (ObjectUtils.isEmpty(notesRequestModel.getNoteDescription())) {
            error.put("description", "Description is required");
        } else {
            if (notesRequestModel.getNoteDescription().length() < 10) {
                error.put("description", "Description length min 10 characters");
            }
            if (notesRequestModel.getNoteDescription().length() > 500) {
                error.put("description", "Description length max 500 characters");
            }
        }

        // Validate category object
        if (ObjectUtils.isEmpty(notesRequestModel.getCategoryId())) {
            error.put("category", "Category Id is required");
        }

        if (!error.isEmpty()) {
            throw new ValidationException(error);
        }
    }

    public void toDoValidation(TodoRequest todoRequest){
        Map<String, Object> error = new LinkedHashMap<>();

        // Validate request object
        if (ObjectUtils.isEmpty(todoRequest)) {
            throw new IllegalArgumentException("ToDo Object/JSON shouldn't be null or empty");
        }

        // Validate title
        if (ObjectUtils.isEmpty(todoRequest.getTitle())) {
            error.put("title", "Title is required");
        } else {
            if (todoRequest.getTitle().length() < 2) {
                error.put("title", "Title length min 2 characters");
            }
            if (todoRequest.getTitle().length() > 500) {
                error.put("title", "Title length max 500 characters");
            }
        }

        // Validate Priority
        if (ObjectUtils.isEmpty(todoRequest.getPriority())) {
            error.put("Priority", "Priority is required");
        } else {
            if (todoRequest.getPriority() == null) {
                error.put("priority", "Priority code is required");
            }
        }

        // Validate category object
        if (ObjectUtils.isEmpty(todoRequest.getStatus())) {
            error.put("Status", "Status Id is required");
        }else {
                try {
                    TodoStatus.fromCode(todoRequest.getStatus());
                } catch (IllegalArgumentException e) {
                    error.put("status", "Invalid status code. " +
                            " Allowed: " +
                            " 1 = NOT_STARTED," +
                            " 2 = IN_PROCESS," +
                            " 3 = COMPLETE");
                }
        }

        if (!error.isEmpty()) {
            throw new ValidationException(error);
        }
    }

    //for user form @valid and MethodArgumentNotValidException this will be handle no need to validate it again here

}
