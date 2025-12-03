package com.enotes.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotesListResponseModel {

    private Integer id;
    private String title;
    private String description;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

}
