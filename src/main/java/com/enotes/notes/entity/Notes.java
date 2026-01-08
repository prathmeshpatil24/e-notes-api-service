package com.enotes.notes.entity;

import com.enotes.utils.entity.BaseModel;
import com.enotes.category.entity.Category;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "notes")
@EntityListeners(AuditingEntityListener.class)
public class Notes extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notes_id")
    private  Integer id;

    @Column(name = "notes_title")
    @NotBlank(message = "Category name must not be blank")
    @Size(min = 2, max = 100, message = "Title name must be between 2 and 100 characters")
    private String title;

    @Column(name = "description")
    @NotEmpty(message = "Description must not be empty")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @Column(name = "isDeleted", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isDeleted = false;

    @Column(name = "isFavorite", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isFavorite = false;

    @Column(name = "deletedAt", nullable = true)
    private LocalDateTime deletedAt;


    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonBackReference
    private Category category;

    @OneToMany(mappedBy = "notes",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<FileEntity> fileEntity = new ArrayList<>();

    public Notes() {}

}
