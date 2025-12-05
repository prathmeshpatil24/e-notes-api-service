package com.enotes.service.impl;

import com.enotes.dto.PaginationResponse;
import com.enotes.dto.ToDoResponse;
import com.enotes.dto.ToDoSummaryResponse;
import com.enotes.dto.TodoRequest;
import com.enotes.entity.Priority;
import com.enotes.entity.TodoStatus;
import com.enotes.repo.ToDoRepo;
import com.enotes.service.ToDoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ToDoServiceImpl implements ToDoService {

    @Autowired
    private ToDoRepo toDoRepo;

    @Override
    public ToDoResponse createTodo(TodoRequest request, Integer userId) {
        return null;
    }

    @Override
    public ToDoResponse updateTodo(Integer id, TodoRequest request, Integer userId) {
        return null;
    }

    @Override
    public PaginationResponse<ToDoResponse> getAllTodos(Integer userId, Integer page, Integer size, String status, String priority) {
        return null;
    }

    @Override
    public ToDoResponse getTodoById(Integer id, Integer userId) {
        return null;
    }

    @Override
    public void softDeleteTodo(Integer id, Integer userId) {

    }

    @Override
    public PaginationResponse<ToDoResponse> getDeletedTodos(Integer userId, Integer page, Integer size) {
        return null;
    }

    @Override
    public void restoreTodo(Integer id, Integer userId) {

    }

    @Override
    public void hardDeleteTodo(Integer id, Integer userId) {

    }

    @Override
    public void clearBin(Integer userId) {

    }

    @Override
    public ToDoSummaryResponse getSummary(Integer userId) {
        return null;
    }

    @Override
    public void updateStatus(Integer id, TodoStatus status, Integer userId) {

    }

    @Override
    public void updatePriority(Integer id, Priority priority, Integer userId) {

    }
}
