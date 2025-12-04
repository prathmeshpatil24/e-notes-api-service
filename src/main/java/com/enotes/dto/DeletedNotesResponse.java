package com.enotes.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class DeletedNotesResponse {

    private Integer notesId;
    private String title;
    private String description;
    private LocalDateTime deletedAt;
    private List<DeletedFileResponse> files;
}
