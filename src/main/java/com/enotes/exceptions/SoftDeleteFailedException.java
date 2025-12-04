package com.enotes.exceptions;

public class SoftDeleteFailedException extends RuntimeException {

    public SoftDeleteFailedException(String s) {
      super(s);
    }
}
