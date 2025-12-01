package com.enotes.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "file_details")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private  Integer fileId;

    @Column(name = "upload_file_name")
    private String uploadFileName;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "display_file_name")
    private String displayFileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @ManyToOne
    @JoinColumn(name = "notes_id")
    @JsonBackReference
    private Notes notes;

    public FileEntity(Integer fileId,
                      String uploadFileName,
                      String originalFileName,
                      String displayFileName,
                      String filePath,
                      Long fileSize,
                      Notes notes) {
        this.fileId = fileId;
        this.uploadFileName = uploadFileName;
        this.originalFileName = originalFileName;
        this.displayFileName = displayFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.notes = notes;
    }

    public FileEntity() {
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public String getUploadFileName() {
        return uploadFileName;
    }

    public void setUploadFileName(String uploadFileName) {
        this.uploadFileName = uploadFileName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getDisplayFileName() {
        return displayFileName;
    }

    public void setDisplayFileName(String displayFileName) {
        this.displayFileName = displayFileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Notes getNotes() {
        return notes;
    }

    public void setNotes(Notes notes) {
        this.notes = notes;
    }
}
