package com.enotes.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "category")
public class Category extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer id;

    @Column(name = "category_name")
    @NotBlank(message = "Category name must not be blank")
    @Size(min = 2, max = 100, message = "Category name must be between 5 and 100 characters")
    private String name;

    @Column(name = "description")
    @NotEmpty(message = "Description must not be empty")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @Column(name = "is_active")
    @NotNull(message = "isActive must not be null")
    private Boolean isActive;

    @OneToMany(mappedBy = "category",
//            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Notes> notes = new ArrayList<>();

    public Category(Integer id,
                    String name,
                    String description,
                    Boolean isActive
                    )
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
    }

    public Category() {}

    public Integer getCategoryId() {
        return id;
    }

    public void setCategoryId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public List<Notes> getNotesList() {
        return notes;
    }

    public void setNotes(List<Notes> notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\''+
                "isActive=" + isActive +
                '}';
    }
}
