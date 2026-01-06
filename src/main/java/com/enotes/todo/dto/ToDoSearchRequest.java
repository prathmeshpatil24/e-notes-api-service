package com.enotes.todo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToDoSearchRequest {

    private Integer userId;
    private Integer toDoId;
    private String title;
    private Integer status;  // 1, 2, or 3
    private String priority; // "LOW", "MEDIUM", "HIGH"


}
