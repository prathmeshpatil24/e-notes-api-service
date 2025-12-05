package com.enotes.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class ToDoResponse {

    private Integer id;
    private String title;

    private String priority;     // LOW / MEDIUM / HIGH

    private Integer statusCode;  // 1, 2, 3
    private String statusLabel;  // Not Started, In Process, Complete

    private LocalDateTime createdOn; // From auditing
}
