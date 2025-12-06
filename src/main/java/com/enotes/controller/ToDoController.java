package com.enotes.controller;

import com.enotes.cofig.AuditAwareConfig;
import com.enotes.dto.*;
import com.enotes.enums.Priority;
import com.enotes.enums.TodoStatus;
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
    public ResponseEntity<?> createToDo(@RequestBody TodoRequest request) {

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
    public ResponseEntity<?> updateToDo(Integer toDoId, TodoRequest request) {
        // Fetch logged-in user ID
        // Hardcoded user ID for demonstration purposes
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"
                ));

        ToDoResponse toDoResponse = toDoService.updateTodo(toDoId, request, userId);

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
    public ResponseEntity<?> getToDoById(@PathVariable Integer id) {
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


    @DeleteMapping("/{id}/move-to-trash")
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


    //in further create one bin for notes and to-do module
    @GetMapping("/recycle-bin")
    public ResponseEntity<?> getToDoRecycleBin(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {

        // Fetch logged-in user ID
        // Hardcoded user ID for demonstration purposes
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"
                ));

        PaginationResponse<ToDoResponse> recycleBin = toDoService.getDeletedTodos(userId,
                pageNo,
                pageSize,
                sortDir,
                sortBy);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "message", "ToDo title list fetched successfully.",
                "data", recycleBin,
                "status", HttpStatus.OK
        ));
    }

    @PutMapping("/recycle-bin/restore/{id}")
    public ResponseEntity<?> restoreToDoById(@PathVariable Integer id) {

        // Fetch logged-in user ID
        // Hardcoded user ID for demonstration purposes
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"
                ));

        toDoService.restoreTodo(id, userId);
        return ResponseEntity.status(HttpStatus.OK).
                body(Map.of(
                        "message", "To-DO restored from trash successfully with ID: " + id,
                        "status", HttpStatus.OK
                ));
    }

    @DeleteMapping("recycle-bin/{id}/delete")
    public ResponseEntity<?> hardDeleteToDoById(Integer id) {

        // Fetch logged-in user ID
        // Hardcoded user ID for demonstration purposes
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"
                ));

        toDoService.hardDeleteTodo(id, userId);


        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "message", "Notes deleted permanently with ID: " + id,
                "status", HttpStatus.OK.value()
        ));
    }

    // test remaining
    @DeleteMapping("/recycle-bin/empty")
    public ResponseEntity<?> emptyRecycleBin() {

        // Fetch logged-in user ID
        // Hardcoded user ID for demonstration purposes
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"));

        toDoService.emptyRecycleBin(userId);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "message", "Recycle bin emptied successfully!",
                "status", HttpStatus.OK.value()

        ));

    }

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary() {

        // Fetch logged-in user ID
        // Hardcoded user ID for demonstration purposes
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"));

        ToDoSummaryResponse response = toDoService.getSummary(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", "Getting Summary",
                        "data", response,
                        "status", HttpStatus.OK.value()
                ));
    }


    //    {
//        "status": "IN_PROCESS"
//    }
    @PatchMapping("/update-status/{id}")
    public ResponseEntity<?> updateStatus(
            @PathVariable Integer id,
            @RequestBody TodoStatus todoStatus

    ) {

        // Fetch logged-in user ID
        // Hardcoded user ID for demonstration purposes
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"));


        toDoService.updateStatus(id, todoStatus, userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", "Status updated successfully for id:- " + id,
                        "data", todoStatus.getCode() + todoStatus.getLabel(),
                        "status", HttpStatus.OK.value()
                ));

    }


    //    {
//        "priority": "HIGH"
//    }
    @PatchMapping("/update-priority/{id}")
    public ResponseEntity<?> updatePriority(
            @PathVariable Integer id,
            @RequestBody Priority priority

    ) {

        // Fetch logged-in user ID
        // Hardcoded user ID for demonstration purposes
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"));


        toDoService.updatePriority(id, priority, userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", "Priority updated successfully for id:- " + id,
                        "data", priority,
                        "status", HttpStatus.OK.value()
                ));
    }

}
