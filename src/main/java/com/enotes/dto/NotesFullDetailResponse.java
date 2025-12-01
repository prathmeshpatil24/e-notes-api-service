package com.enotes.dto;

import java.time.LocalDateTime;
import java.util.List;

public class NotesFullDetailResponse {

    private Integer notesId;
    private String title;
    private String description;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private List<FileDetailsResponse> files;

    public Integer getNotesId() {
        return notesId;
    }

    public void setNotesId(Integer notesId) {
        this.notesId = notesId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public List<FileDetailsResponse> getFiles() {
        return files;
    }

    public void setFiles(List<FileDetailsResponse> files) {
        this.files = files;
    }
}
