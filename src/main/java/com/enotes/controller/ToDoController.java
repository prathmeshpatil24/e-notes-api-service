package com.enotes.controller;

import com.enotes.cofig.AuditAwareConfig;
import com.enotes.dto.NotesListResponseModel;
import com.enotes.dto.PaginationResponse;
import com.enotes.dto.ToDoResponse;
import com.enotes.dto.TodoRequest;
import com.enotes.exceptions.UserNotFoundException;
import com.enotes.service.impl.ToDoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/to-do")
public class ToDoController {

    @Autowired
    private ToDoServiceImpl toDoService;

    @Autowired
    private AuditAwareConfig auditAwareConfig;


    @PostMapping("/create")
    public ResponseEntity<?> createToDo(@RequestBody TodoRequest request){

        // Fetch logged-in user ID
        // Hardcoded user ID for demonstration purposes
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"
                ));

        ToDoResponse toDoResponse = toDoService.createTodo(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "New TODO created successfully",
                        "data", Map.of(
                                "id", toDoResponse.getId(),
                                "title", toDoResponse.getTitle(),
                                "Priority", toDoResponse.getPriority(),
                                "toDo-status", toDoResponse.getStatusLabel()
                        ),
                        "status", HttpStatus.CREATED.value()
                ));
    }


    @PutMapping("/update/{toDoId}")
    public ResponseEntity<?> updateToDo(Integer toDoId, TodoRequest request){
        // Fetch logged-in user ID
        // Hardcoded user ID for demonstration purposes
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"
                ));

        ToDoResponse toDoResponse = toDoService.updateTodo(toDoId ,request, userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", "TODO updated successfully with Id:- " + toDoResponse.getId(),
                        "data", Map.of(
                                "title", toDoResponse.getTitle(),
                                "Priority", toDoResponse.getPriority(),
                                "toDo-status", toDoResponse.getStatusLabel()
                        ),
                        "status", HttpStatus.OK.value()
                ));
    }

    @GetMapping("/toDo-list")
    public ResponseEntity<?> getToDoList(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status, // for filtering
            @RequestParam(required = false) String priority // for Filtering
            ) {

        // Fetch logged-in user ID
        // Hardcoded user ID for demonstration purposes
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"
                ));

//        System.out.println("SORT DIR = '" + sortDir + "'");

        PaginationResponse<ToDoResponse> toDoList = toDoService.getAllTodos(userId,
                pageNo,
                pageSize,
                sortDir,
                sortBy,
                status,
                priority);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "message", "ToDo title list fetched successfully.",
                "data", toDoList,
                "status", HttpStatus.OK
        ));
    }


    @GetMapping("/getToDoById/{id}")
    public ResponseEntity<?> getToDoById(@PathVariable Integer id){
        // Fetch logged-in user ID
        // Hardcoded user ID for demonstration purposes
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"
                ));

        ToDoResponse response = toDoService.getTodoById(id, userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "status", HttpStatus.OK.value(),
                        "data", response
                ));
    }


    @DeleteMapping("{id}/move-to-trash")
    public ResponseEntity<?> softDeleteToDoById(@PathVariable Integer id) {

        // Fetch logged-in user ID
        // Hardcoded user ID for demonstration purposes
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"
                ));

        toDoService.softDeleteTodo(id, userId);
        return ResponseEntity.status(HttpStatus.OK).
                body(Map.of(
                        "message", "To-DO moved to trash successfully with ID: " + id,
                        "status", HttpStatus.OK
                ));
    }

}
