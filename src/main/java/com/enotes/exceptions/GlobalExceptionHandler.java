package com.enotes.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<?> handleCategoryNotFound(CategoryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "status", HttpStatus.NOT_FOUND.value(),
                        "error", "Category Not Found",
                        "message", ex.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
    }

    @ExceptionHandler(CategoryListException.class)
    public ResponseEntity<?> handleCategoryList(CategoryListException ex) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(Map.of(
                      "status",  HttpStatus.NO_CONTENT.value(),
                       "error", "No Categories present",
                       "message", ex.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationData(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "message", ex.getMessage(),
                        "error", ex.getErrors()
                        , "timestamp", LocalDateTime.now()

                ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDuplicateData(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                       "status" ,HttpStatus.CONFLICT.value(),
                       "error", "Duplicate/Invalid Data",
                       "message", ex.getMessage(),
                       "timestamp", LocalDateTime.now()
                ));
    }

    @ExceptionHandler(SaveFailedException.class)
    public ResponseEntity<?> handleSaveFailed(SaveFailedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                       "status", HttpStatus.BAD_REQUEST.value(),
                       "error", "Failed to Save",
                        "message", ex.getMessage(),
                     "timestamp", LocalDateTime.now()
                ));
    }

    @ExceptionHandler(InActiveCategoryException.class)
    public ResponseEntity<?> handleInActiveCategoryException(InActiveCategoryException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", "Failed to Save",
                        "message", ex.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
    }

    @ExceptionHandler(InvalidPaginationParameterException.class)
    public ResponseEntity<?> handleInvalidPaginationException(InvalidPaginationParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", "Failed to retried",
                        "message", ex.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
    }

    @ExceptionHandler(NotesListFetchException.class)
    public ResponseEntity<?> handleNotesListFetchException(NotesListFetchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", "Failed to retried",
                        "message", ex.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "status", HttpStatus.UNAUTHORIZED.value(),
                        "error", "Failed to login",
                        "message", ex.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
    }

    @ExceptionHandler(NotesNotFoundException.class)
    public ResponseEntity<?> handleNotesNotFoundException(NotesNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "status", HttpStatus.NOT_FOUND.value(),
                        "error", "Notes Id not found",
                        "message", ex.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
    }

    @ExceptionHandler(InvaildInputData.class)
    public ResponseEntity<?> handleInvalidInputException(InvaildInputData ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", "Invalid Input data",
                        "message", ex.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
    }

    @ExceptionHandler(SoftDeleteFailedException.class)
    public ResponseEntity<?> handleSoftDeleteException(SoftDeleteFailedException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "message", ex.getMessage(),
                        "error", "Failed to move to recycle bin",
                        "timestamp", LocalDateTime.now()
                ));
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<?> handleFileNotFoundException(FileNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", ex.getMessage(),
                        "message", "File not found in db",
                        "timestamp", LocalDateTime.now()
                ));
    }

    @ExceptionHandler(FileNotesMismatchException.class)
    public ResponseEntity<?> handleFileNotesMismatchException(FileNotesMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", ex.getMessage(),
                        "message", "File Id and NotesId Mismatched",
                        "timestamp", LocalDateTime.now()
                ));
    }

@ExceptionHandler(UserNotesIdException.class)
    public ResponseEntity<?>handleUserNotesException(UserNotesIdException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", "Invalid Data",
                        "message", ex.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobal(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                       "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "error", "Internal Server Error",
                        "message", ex.getMessage(),
                      "timestamp", LocalDateTime.now()
                ));
    }


}
