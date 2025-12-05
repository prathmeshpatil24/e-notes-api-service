package com.enotes.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DeletedFileResponse {

    private Integer fileId;
    private String fileName;
    private Double fileSize;
    private LocalDateTime deletedAt;
    private Integer notesId;
    private String notesTitle;
}
