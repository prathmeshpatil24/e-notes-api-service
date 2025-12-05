package com.enotes.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ToDoSummaryResponse {

    private Long total;
    private Long completed;
    private Long inProcess;
    private Long notStarted;

    private Long highPriority;
}
