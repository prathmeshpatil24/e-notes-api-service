package com.enotes.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStatusRequest {
    private Integer status;  // 1, 2, or 3
}
