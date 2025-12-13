package com.enotes.todo.entity;

import com.enotes.cofig.TodoStatusConverter;
import com.enotes.entity.BaseModel;
import com.enotes.todo.enums.Priority;
import com.enotes.todo.enums.TodoStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "todo")
public class ToDo extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "To-Do title must not be blank")
    @Size(min = 2, max = 100, message = "Title name must be between 2 and 100 characters")
    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    private Priority priority;  // Stored as VARCHAR

    @Convert(converter = TodoStatusConverter.class)
    private TodoStatus status;  // Stored as INT

    @Column(name = "deletedAt", nullable = true)
    private LocalDateTime deletedAt;

    @Column(name = "isDeleted", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isDeleted = false;

    public ToDo() {}
}
