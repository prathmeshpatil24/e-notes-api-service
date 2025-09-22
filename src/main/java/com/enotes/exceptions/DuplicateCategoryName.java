package com.enotes.exceptions;

public class DuplicateCategoryName extends RuntimeException{

    public DuplicateCategoryName(String message) {
        super(message);
    }
}
