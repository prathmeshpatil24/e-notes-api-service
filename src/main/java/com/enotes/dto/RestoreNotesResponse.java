package com.enotes.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class RestoreNotesResponse {
    private Integer restoredNoteId;
    private String message;
    private LocalDateTime restoredAt;
}
