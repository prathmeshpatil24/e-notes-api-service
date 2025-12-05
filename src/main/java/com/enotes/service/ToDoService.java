package com.enotes.service;


import com.enotes.dto.PaginationResponse;
import com.enotes.dto.ToDoSummaryResponse;
import com.enotes.dto.TodoRequest;
import com.enotes.dto.ToDoResponse;
import com.enotes.entity.Priority;
import com.enotes.entity.TodoStatus;
import org.springframework.data.domain.Page;


public interface ToDoService {

    ToDoResponse createTodo(TodoRequest request, Integer userId);

    ToDoResponse updateTodo(Integer id, TodoRequest request, Integer userId);

    PaginationResponse<ToDoResponse> getAllTodos(Integer userId, Integer page, Integer size, String status, String priority);

    ToDoResponse getTodoById(Integer id, Integer userId);

    void softDeleteTodo(Integer id, Integer userId);

    PaginationResponse<ToDoResponse> getDeletedTodos(Integer userId, Integer page, Integer size);

    void restoreTodo(Integer id, Integer userId);

    void hardDeleteTodo(Integer id, Integer userId);

    void clearBin(Integer userId);

    ToDoSummaryResponse getSummary(Integer userId);

    void updateStatus(Integer id, TodoStatus status, Integer userId);

    void updatePriority(Integer id, Priority priority, Integer userId);
}
