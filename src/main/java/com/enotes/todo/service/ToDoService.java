package com.enotes.todo.service;


import com.enotes.dto.PaginationResponse;
import com.enotes.dto.ToDoSummaryResponse;
import com.enotes.dto.TodoRequest;
import com.enotes.dto.ToDoResponse;
import com.enotes.todo.enums.Priority;
import com.enotes.todo.enums.TodoStatus;


public interface ToDoService {

    ToDoResponse createTodo(TodoRequest request, Integer userId);

    ToDoResponse updateTodo(Integer id,
                            TodoRequest request,
                            Integer userId);

    PaginationResponse<ToDoResponse> getAllTodos(Integer userId,
                                                 Integer pageNo,
                                                 Integer pageSize,
                                                 String sortDir,
                                                 String sortBy,
                                                 String status,
                                                 String priority);

    ToDoResponse getTodoById(Integer id, Integer userId);

    void softDeleteTodo(Integer id, Integer userId);

    PaginationResponse<ToDoResponse> getDeletedTodos(Integer userId,
                                                     Integer pageNo,
                                                     Integer pageSize,
                                                     String SortBy,
                                                     String SortDir);

    void restoreTodo(Integer id, Integer userId);

    void hardDeleteTodo(Integer id, Integer userId);

    String emptyRecycleBin(Integer userId);

    ToDoSummaryResponse getSummary(Integer userId);

    void updateStatus(Integer id,
                      TodoStatus status,
                      Integer userId);

    void updatePriority(Integer id,
                        Priority priority,
                        Integer userId);
}
