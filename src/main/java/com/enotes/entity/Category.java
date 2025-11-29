package com.enotes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
@Table(name = "category")
public class Category extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer id;

    @Column(name = "category_name")
    @NotBlank(message = "Category name must not be blank")
    @Size(min = 5, max = 100, message = "Category name must be between 5 and 100 characters")
    private String name;

    @Column(name = "description")
    @NotEmpty(message = "Description must not be empty")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @Column(name = "is_active")
    @NotNull(message = "isActive must not be null")
    private Boolean isActive;

//    @Column(name = "is_deleted")
//    private  Boolean isDeleted;


    public Category(Integer id,
                    String name,
                    String description,
                    Boolean isActive
//                    Boolean isDeleted
                    )
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
//        this.isDeleted = isDeleted;
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

//    public Boolean getIsDeleted() {
//        return isDeleted;
//    }
//
//    public void setIsDeleted(Boolean deleted) {
//        isDeleted = deleted;
//    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\''+
                "isActive=" + isActive +
//                ", isDeleted=" + isDeleted +
                '}';
    }
}
