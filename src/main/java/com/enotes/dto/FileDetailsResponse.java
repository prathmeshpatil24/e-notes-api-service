package com.enotes.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FileDetailsResponse {
    private Integer notesId;
    private Integer fileId;
//    private String displayFileName;
    private String fileName;
    private Double fileSize;
    private Integer createdBy;
    private Integer updatedBy;
    private Boolean isFavorite;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
