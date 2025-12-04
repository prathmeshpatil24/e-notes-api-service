package com.enotes.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class TrashResponse {
    private List<DeletedNotesResponse> deletedNotes;
    private List<DeletedFileResponse> deletedFiles;
}
