package com.enotes.exceptions;

import java.io.IOException;

public class FileHandlingException extends Throwable {

    public FileHandlingException(String s, IOException ioException) {
        super(s, ioException);
    }
}
