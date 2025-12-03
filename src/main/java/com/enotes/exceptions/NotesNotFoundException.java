package com.enotes.exceptions;

public class NotesNotFoundException extends RuntimeException {

    public NotesNotFoundException(Integer notesId) {
        super("Notes not found with ID: " + notesId);
    }

    public NotesNotFoundException() {
        super("Notes not found");
    }
}
