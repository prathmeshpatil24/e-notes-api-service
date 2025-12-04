package com.enotes.dto;

import com.enotes.entity.Notes;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotesRequestModel {

    private String noteTitle;
    private String noteDescription;
    private Integer categoryId;

}
