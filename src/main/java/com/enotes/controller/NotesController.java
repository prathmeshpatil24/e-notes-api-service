package com.enotes.controller;

import com.enotes.cofig.AuditAwareConfig;
import com.enotes.dto.NotesListResponseModel;
import com.enotes.dto.NotesRequestModel;
import com.enotes.dto.PaginationResponse;
import com.enotes.service.impl.NotesServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notes")
public class NotesController {

    @Autowired
    private NotesServiceImpl notesService;

    @Autowired
    private AuditAwareConfig auditAwareConfig;

    @PostMapping("/create")
    public ResponseEntity<?>createNewNotes(@RequestBody NotesRequestModel notesRequestModel){

        try {
            NotesRequestModel notes = notesService.createNotes(notesRequestModel);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Notes created successfully with title: " + notes.getNoteTitle(),
                    "status", HttpStatus.CREATED
            ));
        } catch (Exception e) {
            System.out.println("Error creating notes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Failed to create notes. ",
                    "status", HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/notes-title-list")
    public  ResponseEntity<?> getNotesListWithTitleAndDescription(
            @RequestParam Integer pageNo,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir)
    {
        try {
            Optional<Integer> currentAuditor = auditAwareConfig.getCurrentAuditor();// Hardcoded user ID for demonstration purposes

            Integer userId = currentAuditor.orElseThrow(
                    () -> new RuntimeException("User not authenticated")
            );

            PaginationResponse<NotesListResponseModel> notesList = notesService.getNotesTitleList(userId, pageNo, pageSize, sortBy, sortDir);

            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "message", "Notes title list fetched successfully.",
                    "data", notesList,
                    "status", HttpStatus.OK
            ));
        }catch (Exception ex){

            System.out.println("Error fetching notes title list: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Failed to fetch notes title list.",
                    "status", HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }
}
