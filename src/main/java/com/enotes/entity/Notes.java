package com.enotes.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "notes")
public class Notes extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notes_id")
    private  Integer id;


    @Column(name = "notes_title")
    private String title;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonBackReference
    private Category category;

    @OneToMany(mappedBy = "notes",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<FileEntity> fileEntity = new ArrayList<>();

    public Notes(Integer id,
                 String title,
                 String description,
                 Category category,
                 List<FileEntity> fileEntity
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.fileEntity = fileEntity;
    }



    public Notes() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<FileEntity> getFileEntity() {
        return fileEntity;
    }

    public void setFileEntity(List<FileEntity> fileEntity) {
        this.fileEntity = fileEntity;
    }
}
