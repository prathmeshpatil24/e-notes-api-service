package com.enotes.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotesRequestModel {

    private String noteTitle;
    private String noteDescription;
    private Integer categoryId;

}
