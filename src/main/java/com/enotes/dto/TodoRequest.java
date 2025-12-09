package com.enotes.dto;

import com.enotes.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoRequest {

    @NotBlank(message = "Title is required")
    private String title;

    //send priority →  as STRING
    // Priority: LOW, MEDIUM, HIGH
    @NotNull(message = "Priority is required")
    private Priority priority;

    // send status -> INTEGER
    // Status: 1=NOT_STARTED, 2=INPROCESS, 3=COMPLETE
    @NotNull(message = "Status code is required")
    private Integer status;

}
