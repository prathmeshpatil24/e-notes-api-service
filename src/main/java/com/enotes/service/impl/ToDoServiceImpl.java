package com.enotes.service.impl;

import com.enotes.dto.*;
import com.enotes.entity.ToDo;
import com.enotes.enums.Priority;
import com.enotes.enums.TodoStatus;
import com.enotes.exceptions.InvalidPaginationParameterException;
import com.enotes.exceptions.SaveFailedException;
import com.enotes.exceptions.ToDoException;
import com.enotes.exceptions.ToDoListFetchException;
import com.enotes.repo.ToDoRepo;
import com.enotes.service.ToDoService;
import com.enotes.utils.Validation;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ToDoServiceImpl implements ToDoService {

    @Autowired
    private ToDoRepo toDoRepo;

    @Autowired
    private Validation validation;

    @Override
    public ToDoResponse createTodo(TodoRequest request, Integer userId) {

        //validation
        validation.toDoValidation(request);

        ToDo toDo = new ToDo();
        toDo.setTitle(request.getTitle());
        toDo.setPriority(request.getPriority());
        toDo.setStatus(TodoStatus.fromCode(request.getStatus()));

        try {
            // saving in db
            ToDo saved = toDoRepo.save(toDo);

            //giving response
            ToDoResponse response = new ToDoResponse();
            response.setId(saved.getId());
            response.setTitle(saved.getTitle());
            response.setPriority(saved.getPriority().name());
            response.setStatusLabel(saved.getStatus().getLabel());
            response.setStatusCode(saved.getStatus().getCode());
            response.setCreatedAt(saved.getCreatedAt());

            return response;
        } catch (Exception ex) {
            throw new SaveFailedException("ToDo save failed:" + ex);
        }

    }

    //update All data from toDo
    @Override
    public ToDoResponse updateTodo(Integer id, TodoRequest request,
                                   Integer userId) {
        ToDo todo = toDoRepo.findByIdAndCreatedByAndIsDeletedFalse(id, userId)
                .orElseThrow(() -> new ToDoException("Todo not found with UserId:- " + userId +
                        " and toDo id:- " + id));

        todo.setTitle(request.getTitle());
        todo.setPriority(request.getPriority());
        todo.setStatus(TodoStatus.fromCode(request.getStatus()));

        try {
            ToDo updated = toDoRepo.save(todo);

            //giving response
            ToDoResponse response = new ToDoResponse();
            response.setId(updated.getId());
            response.setTitle(updated.getTitle());
            response.setPriority(updated.getPriority().name());
            response.setStatusLabel(updated.getStatus().getLabel());
            response.setStatusCode(updated.getStatus().getCode());
            response.setCreatedAt(updated.getCreatedAt());

            return response;
        } catch (Exception ex) {
            throw new SaveFailedException("ToDo updation failed:" + ex);
        }
    }

    @Override
    public PaginationResponse<ToDoResponse> getAllTodos(Integer userId,
                                                        Integer pageNo,
                                                        Integer pageSize,
                                                        String sortDir,
                                                        String sortBy,
                                                        String status, // for filtering
                                                        String priority // for filtering
    ) {

        // Validate pagination params
        if (pageNo < 0) {
            throw new InvalidPaginationParameterException("Page index must not be negative");
        }
        if (pageSize <= 0) {
            throw new InvalidPaginationParameterException("Page size must be greater than zero");
        }
        if (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {
            throw new InvalidPaginationParameterException("Sort direction must be 'asc' or 'desc'");
        }


        try {
            //created sorting object
            Sort sort = sortDir.equalsIgnoreCase("asc") ?
                    Sort.by(sortBy).ascending() :
                    Sort.by(sortBy).descending();

            //created pageable object
            Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

            /*
        if(status != null) apply status filter;
        if(priority != null) apply priority filter;
        Page<Todo> todos;

        if (status != null) {
            todos = todoRepo.findByCreatedByAndStatusAndIsDeletedFalse(
                    userId, TodoStatus.valueOf(status), pageable);
        }
        else if (priority != null) {
            todos = todoRepo.findByCreatedByAndPriorityAndIsDeletedFalse(
                    userId, Priority.valueOf(priority), pageable);
        }
        */

            Page<ToDo> toDoPage = toDoRepo.findByCreatedByAndIsDeletedFalse(userId, pageable);

            //validate page number does not exceed total pages
            int totalPages = toDoPage.getTotalPages();
            if (totalPages > 0 && pageNo >= totalPages) {
                throw new InvalidPaginationParameterException(
                        "Page number " + pageNo + " exceeds the maximum available pages: " + (totalPages - 1)
                );
            }

            //convert entity page to dto page
            Page<ToDoResponse> toDoResponsePage = toDoPage
                    .map(
                            toDo ->
                            {
                                ToDoResponse dto = new ToDoResponse();
                                dto.setId(toDo.getId());
                                dto.setTitle(toDo.getTitle());
                                dto.setPriority(toDo.getPriority().name());
                                dto.setStatusLabel(toDo.getStatus().getLabel());
                                dto.setStatusCode(toDo.getStatus().getCode());
                                dto.setCreatedAt(toDo.getCreatedAt());
                                dto.setUpdatedAt(toDo.getUpdatedAt());

                                return dto;
                            });
            //further add file counts also

            return new PaginationResponse<>(toDoResponsePage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ToDoListFetchException("Failed for fetching ToDo list. " + e.getMessage());
        }

    }

    @Override
    public ToDoResponse getTodoById(Integer id, Integer userId) {

        ToDo todo = toDoRepo.findByIdAndCreatedByAndIsDeletedFalse(id, userId)
                .orElseThrow(() -> new ToDoException("Todo not found with Id:- " + id));

        try {

            ToDoResponse response = new ToDoResponse();
            response.setId(todo.getId());
            response.setTitle(todo.getTitle());
            response.setPriority(todo.getPriority().name());
            response.setStatusLabel(todo.getStatus().getLabel());
            response.setStatusCode(todo.getStatus().getCode());
            response.setCreatedAt(todo.getCreatedAt());
            response.setUpdatedAt(todo.getUpdatedAt());

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error" , e);
        }
    }

    @Transactional
    @Override
    public void softDeleteTodo(Integer id, Integer userId) {

        ToDo todo = toDoRepo.findByIdAndCreatedByAndIsDeletedFalse(id, userId)
                .orElseThrow(() -> new ToDoException("Todo not found with id:- " + id));
        try {

            todo.setIsDeleted(true);
            todo.setDeletedAt(LocalDateTime.now());
            toDoRepo.save(todo);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected error:- ",  e);
        }
    }

    @Override
    public PaginationResponse<ToDoResponse> getDeletedTodos(Integer userId,
                                                            Integer pageNo,
                                                            Integer pageSize,
                                                            String sortBy,
                                                            String sortDir) {
        // Validate pagination params
        if (pageNo < 0) {
            throw new InvalidPaginationParameterException("Page index must not be negative");
        }
        if (pageSize <= 0) {
            throw new InvalidPaginationParameterException("Page size must be greater than zero");
        }
        if (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {
            throw new InvalidPaginationParameterException("Sort direction must be 'asc' or 'desc'");
        }

        try {
            //created sorting object
            Sort sort = sortDir.equalsIgnoreCase("asc") ?
                    Sort.by(sortBy).ascending() :
                    Sort.by(sortBy).descending();

            //created pageable object
            Pageable pageable = PageRequest.of(pageNo, pageSize, sort);


            Page<ToDo> toDoPage = toDoRepo.findByCreatedByAndIsDeletedTrue(userId, pageable);

            // If bin is empty
            if (toDoPage.isEmpty()) {
                return new PaginationResponse<>(
                            Collections.emptyList(),
                            0,
                            0,
                            "No deleted items found"
                    );
                }

            //validate page number does not exceed total pages
            int totalPages = toDoPage.getTotalPages();
            if (totalPages > 0 && pageNo >= totalPages) {
                throw new InvalidPaginationParameterException(
                        "Page number " + pageNo + " exceeds the maximum available pages: " + (totalPages - 1)
                );
            }

            //convert entity page to dto page
            Page<ToDoResponse> toDoResponsePage = toDoPage
                    .map(
                            toDo ->
                            {
                                ToDoResponse dto = new ToDoResponse();
                                dto.setId(toDo.getId());
                                dto.setTitle(toDo.getTitle());
                                dto.setPriority(toDo.getPriority().name());
                                dto.setStatusLabel(toDo.getStatus().getLabel());
                                dto.setStatusCode(toDo.getStatus().getCode());
                                dto.setCreatedAt(toDo.getCreatedAt());
                                dto.setUpdatedAt(toDo.getUpdatedAt());

                                return dto;
                            });
            //further add file counts also

            return new PaginationResponse<>(toDoResponsePage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ToDoListFetchException("Failed for fetching ToDo list. " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public void restoreTodo(Integer id, Integer userId) {

        ToDo todo = toDoRepo.findByIdAndCreatedByAndIsDeletedTrue(id, userId)
                .orElseThrow(() -> new ToDoException("Todo not found with id:- " + id + "in recycle"));
        try {

            todo.setIsDeleted(false);
            todo.setDeletedAt(null);
            toDoRepo.save(todo);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected error:- ",  e);
        }
    }

    @Transactional
    @Override
    public void hardDeleteTodo(Integer id, Integer userId) {

        ToDo todo = toDoRepo.findByIdAndCreatedByAndIsDeletedTrue(id, userId)
                .orElseThrow(() -> new ToDoException("Todo not found in bin with toDoId:- " + id));

        try {

            toDoRepo.delete(todo);

        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error occurred while deleting toDo with id:- " + id);
        }


    }

    @Transactional
    @Override
    public String emptyRecycleBin(Integer userId) {
       try {

           long deletedCount = toDoRepo.countDeletedByUser(userId);

           if (deletedCount == 0) {
               return "No deleted items to clear";
           }

           toDoRepo.deleteAllDeletedByUser(userId);

           return deletedCount + " deleted item(s) permanently removed";
       } catch (Exception e) {
           e.printStackTrace();
           throw new RuntimeException("Error occurred while clearing toDo bin. " , e);
       }
    }

    @Override
    public ToDoSummaryResponse getSummary(Integer userId) {

        try {

            ToDoSummaryResponse res = new ToDoSummaryResponse();

            res.setTotal(toDoRepo.countByCreatedBy(userId));
            res.setCompleted(toDoRepo.countByCreatedByAndStatus(userId, TodoStatus.COMPLETE));
            res.setInProcess(toDoRepo.countByCreatedByAndStatus(userId, TodoStatus.IN_PROCESS));
            res.setNotStarted(toDoRepo.countByCreatedByAndStatus(userId, TodoStatus.NOT_STARTED));
            res.setHighPriority(toDoRepo.countByCreatedByAndPriority(userId, Priority.HIGH));

            return res;

        } catch (Exception e) {
            throw new RuntimeException("Unexpected error:- ",  e);
        }
    }

    @Override
    public void updateStatus(Integer id, TodoStatus status, Integer userId) {

        ToDo todo = toDoRepo.findByIdAndCreatedByAndIsDeletedFalse(id, userId)
                .orElseThrow(() -> new ToDoException("Todo not found with id:- " + id));

       try{

           todo.setStatus(status);
           toDoRepo.save(todo);

       } catch (Exception e) {
           e.printStackTrace();
           throw new RuntimeException("Unexpected error:- ",  e);
       }
    }

    @Override
    public void updatePriority(Integer id, Priority priority, Integer userId) {

        ToDo todo = toDoRepo.findByIdAndCreatedByAndIsDeletedFalse(id, userId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        try {

            todo.setPriority(priority);
            toDoRepo.save(todo);

        } catch (Exception e) {
            throw new RuntimeException("Unexpected error:- ",  e);
        }
    }
}
