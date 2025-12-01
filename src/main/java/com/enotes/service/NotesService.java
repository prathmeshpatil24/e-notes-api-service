package com.enotes.service;

import com.enotes.dto.*;

public interface NotesService {

    //create new notes
    public NotesRequestModel createNotes(NotesRequestModel notesRequestModel);

    public PaginationResponse<NotesListResponseModel> getNotesTitleList(Integer userId, Integer pageNo, Integer pageSize, String sortBy, String sortDir);
}
