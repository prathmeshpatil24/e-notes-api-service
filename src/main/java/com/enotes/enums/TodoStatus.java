package com.enotes.enums;


import lombok.Getter;

@Getter
public enum TodoStatus {

    NOT_STARTED(1, "Not Started"),
    IN_PROCESS(2, "In Process"),
    COMPLETE(3, "Complete");

    private final int code;
    private final String label;

    TodoStatus(int code, String label) {
        this.code = code;
        this.label = label;
    }

    // Convert int → enum
    public static TodoStatus fromCode(int code) {
        for (TodoStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("Invalid status code: " + code);
    }
}
