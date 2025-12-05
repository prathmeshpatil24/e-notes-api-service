package com.enotes.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class NotesFullDetailResponse {

    private Integer notesId;
    private String title;
    private String description;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private Boolean isFavorite;
    private List<FileDetailsResponse> files;


}
