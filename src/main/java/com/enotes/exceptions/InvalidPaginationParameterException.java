package com.enotes.exceptions;

public class InvalidPaginationParameterException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public InvalidPaginationParameterException(String message) {
        super(message);
    }


}
