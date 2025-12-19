package com.enotes.todo.controller;

import com.enotes.audit.AuditAwareConfig;
import com.enotes.dto.*;
import com.enotes.todo.dto.ToDoSearchRequest;
import com.enotes.todo.entity.ToDo;
import com.enotes.todo.enums.Priority;
import com.enotes.todo.enums.TodoStatus;
import com.enotes.exceptions.UserNotFoundException;
import com.enotes.todo.service.ToDoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<?> updateToDo(@PathVariable Integer toDoId, @RequestBody TodoRequest request) {
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

    @GetMapping("/getAll")
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


    //in development
    @GetMapping("/search")
    public ResponseEntity<?> searchToDo(@RequestBody ToDoSearchRequest request){

        // Fetch logged-in user ID
        // Hardcoded user ID for demonstration purposes
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"));

        List<ToDo> toDos = toDoService.dynamicSearchToDo(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("status", HttpStatus.OK.value(),
                        "data", toDos
                ));

    }

    @GetMapping("/getById/{id}")
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

        System.out.println("SORT DIR RECEIVED = " + sortDir);

        PaginationResponse<ToDoResponse> recycleBin = toDoService.getDeletedTodos(
                userId,
                pageNo,
                pageSize,
                sortBy,
                sortDir
                );

        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "message", "ToDo title list fetched successfully.",
                "data", recycleBin,
                "status", HttpStatus.OK
        ));
    }

    @PatchMapping("/recycle-bin/restore/{id}")
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
    public ResponseEntity<?> hardDeleteToDoById(@PathVariable Integer id) {

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

        String recycleBin = toDoService.emptyRecycleBin(userId);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "message", recycleBin,
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
//        "status": 3
//    }
    @PatchMapping("/update-status/{id}")
    public ResponseEntity<?> updateStatus(
            @PathVariable Integer id,
            @RequestBody UpdateStatusRequest req

    ) {

        // Fetch logged-in user ID
        // Hardcoded user ID for demonstration purposes
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"));

        TodoStatus todoStatus = TodoStatus.fromCode(req.getStatus());

        toDoService.updateStatus(id, todoStatus, userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", "Status updated successfully for id:- " + id,
                        "data",  "status:- " + todoStatus.getCode() + " label:- " + todoStatus.getLabel(),
                        "status", HttpStatus.OK.value()
                ));

    }


    //    {
//        "priority": "HIGH"
//    }
    @PatchMapping("/update-priority/{id}")
    public ResponseEntity<?> updatePriority(
            @PathVariable Integer id,
            @RequestBody UpdatePriorityRequest request

    ) {

        // Fetch logged-in user ID
        // Hardcoded user ID for demonstration purposes
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"));

        Priority priority = Priority.valueOf(request.getPriority().toUpperCase());


        toDoService.updatePriority(id, priority, userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", "Priority updated successfully for id:- " + id,
                        "data", priority,
                        "status", HttpStatus.OK.value()
                ));
    }

}
