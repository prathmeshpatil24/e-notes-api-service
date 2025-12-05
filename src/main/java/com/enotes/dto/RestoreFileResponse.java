package com.enotes.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RestoreFileResponse {
    private Integer restoredFileId;
    private Integer associatedNoteId;
    private String message;
    private LocalDateTime restoredAt;
}
