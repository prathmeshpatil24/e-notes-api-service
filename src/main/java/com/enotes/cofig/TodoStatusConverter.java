package com.enotes.cofig;

import com.enotes.enums.TodoStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TodoStatusConverter implements AttributeConverter<TodoStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TodoStatus status) {
        return status != null ? status.getCode() : null;
    }

    @Override
    public TodoStatus convertToEntityAttribute(Integer code) {
        return code != null ? TodoStatus.fromCode(code) : null;
    }
}
