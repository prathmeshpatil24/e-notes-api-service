package com.enotes.service;

import com.enotes.dto.FileDetailsResponse;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface FileService {
    List<FileDetailsResponse> uploadFiles(Integer notesId, List<MultipartFile> files) throws IOException;

    Resource downloadFile(Integer notesId,String fileName)throws FileNotFoundException,
            IOException;

    void deleteFile(String fileName);
}
