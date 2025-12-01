package com.enotes.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "file_details")
public class FileEntity extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private  Integer fileId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_size")
    private Double fileSize;

    @ManyToOne
    @JoinColumn(name = "notes_id")
    @JsonBackReference
    private Notes notes;

    public FileEntity(Integer fileId,
                      String fileName,
                      String filePath,
                      Double fileSize,
                      Notes notes) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.notes = notes;
    }

    public FileEntity() {
    }

    public Integer getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Double getFileSize() {
        return fileSize;
    }

    public void setFileSize(Double fileSize) {
        this.fileSize = fileSize;
    }

    public Notes getNotes() {
        return notes;
    }

    public void setNotes(Notes notes) {
        this.notes = notes;
    }
}
