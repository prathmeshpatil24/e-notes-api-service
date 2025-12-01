package com.enotes.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileDetailsResponse {
    private Integer notesId;
    private Integer fileId;
    private String fileName;
    private Double fileSize;

    public Integer getNotesId(Integer id) {
        return notesId;
    }

    public void setNotesId(Integer notesId) {
        this.notesId = notesId;
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Double getFileSize() {
        return fileSize;
    }

    public void setFileSize(Double fileSize) {
        this.fileSize = fileSize;
    }
}
