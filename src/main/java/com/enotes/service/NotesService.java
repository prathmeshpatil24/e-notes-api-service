package com.enotes.service;

import com.enotes.dto.*;
import com.enotes.entity.Notes;

public interface NotesService {

    //create new notes
    Notes createNotes(NotesRequestModel notesRequestModel);

    //short data of notes with title and description
    PaginationResponse<NotesListResponseModel> getNotesTitleList(Integer userId,
                                                                 Integer pageNo,
                                                                 Integer pageSize,
                                                                 String sortBy,
                                                                 String sortDir);

    //update notes
     Notes updateNotes(Integer noteId, NotesRequestModel notesRequestModel);

     //get whole details of notes with notes id
    NotesFullDetailResponse getNotesFullDetailsByNoteId(Integer noteId);

    void softDeleteNoteById(Integer noteId);

    //get all data which is soft deleted and present under recycle bin
    TrashResponse recycleBin(Integer userId);

    //restoring the deleted notes with attached files
    RestoreNotesResponse restoreNote(Integer noteId, Integer userId);

    void hardDeleteNotesById(Integer noteId, Integer userId);

    void emptyRecycleBin(Integer userId);


}
