package com.enotes.exceptions;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Integer categoryId) {
        super("Category not found with id: " + categoryId);
    }

    public CategoryNotFoundException(String message) {
        super(message);
    }
}

