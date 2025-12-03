package com.enotes.service;

import com.enotes.dto.FileDetailsResponse;

import com.enotes.exceptions.FileHandlingException;
import com.enotes.exceptions.FileNotesMismatchException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface FileService {
    List<FileDetailsResponse> uploadFilesByNotesId(Integer notesId, List<MultipartFile> files) throws FileHandlingException;

    Resource downloadFile(Integer notesId, String fileName)throws IOException;

    void softDeleteFile(Integer fileId,Integer notesId) throws FileNotFoundException, FileNotesMismatchException;

    void hardDeleteFile(Integer fileId);
}
