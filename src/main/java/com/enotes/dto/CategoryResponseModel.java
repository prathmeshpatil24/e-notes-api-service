package com.enotes.dto;

import java.util.Date;

public class CategoryResponseModel {

    private Integer id;
    private String name;
    private String description;
    private Boolean isActive;
    private Boolean isDeleted;
    private Integer createdBy;
    private Integer updatedBy;
    private Date createdAt;
    private Date updatedAt;

    public CategoryResponseModel() {}

    public CategoryResponseModel(Integer id,
                                 String name,
                                 String description,
                                 Boolean isActive,
                                 Boolean isDeleted,
                                 Integer createdBy,
                                 Integer updatedBy,
                                 Date createdAt,
                                 Date updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
        this.isDeleted = isDeleted;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
