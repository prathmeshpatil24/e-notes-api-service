package com.enotes.notes.entity;

import com.enotes.entity.BaseModel;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "file_details")
public class FileEntity extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private  Integer fileId;

    @Column(name = "file_name")
    private String fileName;

//    @Column(name = "display_file_name")
//    private String displayFileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_size")
    private Double fileSize;

    @Column(name = "isDeleted", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isDeleted = false;

    @Column(name = "isFavorite", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isFavorite = false;

    @ManyToOne
    @JoinColumn(name = "notes_id")
    @JsonBackReference
    private Notes notes;

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;

    public FileEntity() {}

}
