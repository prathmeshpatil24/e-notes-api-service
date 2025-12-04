package com.enotes.controller;

import com.enotes.cofig.AuditAwareConfig;
import com.enotes.dto.*;
import com.enotes.entity.Notes;
import com.enotes.exceptions.FileHandlingException;
import com.enotes.exceptions.FileNotesMismatchException;
import com.enotes.exceptions.UserNotFoundException;
import com.enotes.service.impl.FileServiceImpl;
import com.enotes.service.impl.NotesServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
public class NotesController {

    @Autowired
    private NotesServiceImpl notesService;

    @Autowired
    private FileServiceImpl fileService;

    @Autowired
    private AuditAwareConfig auditAwareConfig;

    @PostMapping("/create")
    public ResponseEntity<?> createNewNotes(@RequestBody NotesRequestModel notesRequestModel) {

        Notes notes = notesService.createNotes(notesRequestModel);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Notes created successfully",
                        "data", Map.of(
                                "id", notes.getId(),
                                "title", notes.getTitle(),
                                "category", notes.getCategory().getName()
                        ),
                        "status", HttpStatus.CREATED.value()
                ));
    }

    @GetMapping("/title-list")
    public ResponseEntity<?> getNotesListWithTitleAndDescription(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        // Fetch logged-in user ID
        // Hardcoded user ID for demonstration purposes
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"
                ));

//        System.out.println("SORT DIR = '" + sortDir + "'");

        PaginationResponse<NotesListResponseModel> notesList = notesService.getNotesTitleList(userId, pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "message", "Notes title list fetched successfully.",
                "data", notesList,
                "status", HttpStatus.OK
        ));
    }


    @PutMapping("/update/{noteId}")
    public ResponseEntity<?> updateNotes(
            @PathVariable Integer noteId,
            @RequestBody NotesRequestModel notesRequestModel) {

        Notes updateNotes = notesService.updateNotes(noteId, notesRequestModel);
        return ResponseEntity.status(HttpStatus.OK).body(
                Map.of(
                        "message", "Notes updated successfully with ID: " + updateNotes.getId(),
                        "status", HttpStatus.OK
                ));
    }


    @PostMapping("/{noteId}/upload-files")
    public ResponseEntity<?> uploadFilesToNotes(
            @PathVariable Integer noteId,
            @RequestParam("files") List<MultipartFile> files)
            throws FileHandlingException {

        List<FileDetailsResponse> fileDetailsResponseList = fileService.uploadFilesByNotesId(noteId, files);
        return ResponseEntity.status(HttpStatus.OK).body(
                Map.of(
                        "message", "Files uploaded successfully to notes Id:-" + noteId,
                        "status", HttpStatus.OK,
                        "files", fileDetailsResponseList
                ));
    }

    @GetMapping("/getDetails/{noteId}")
    public ResponseEntity<?> getFullNotesDetails(@PathVariable Integer noteId) {

        NotesFullDetailResponse fullDetailsByNotesId = notesService.getNotesFullDetailsByNoteId(noteId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "status", HttpStatus.OK.value(),
                        "data", fullDetailsByNotesId
                ));
    }

    @GetMapping("/getDetails/{noteId}/view-file/{fileName}")
    public ResponseEntity<?> viewFile(@PathVariable Integer noteId, @PathVariable String fileName) throws IOException {

        var resource = fileService.downloadFile(noteId, fileName);
        String contentType = Files.probeContentType(resource.getFile().toPath());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(resource);

    }

    @GetMapping("/getDetails/{noteId}/download-file/{fileName}")
    public ResponseEntity<?> downloadFile(@PathVariable Integer noteId, @PathVariable String fileName) throws IOException {

        var resource = fileService.downloadFile(noteId, fileName);
        String contentType = Files.probeContentType(resource.getFile().toPath());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);

    }

    @DeleteMapping("{noteId}/move-to-trash")
    public ResponseEntity<?> softDeleteNotesById(@PathVariable Integer noteId) {

        notesService.softDeleteNoteById(noteId);
        return ResponseEntity.status(HttpStatus.OK).
                body(Map.of(
                        "message", "Notes moved to trash successfully with ID: " + noteId,
                        "status", HttpStatus.OK
                ));
    }

    @DeleteMapping("/{noteId}/move-to-trash/file/{fileId}")
    public ResponseEntity<?> softDeleteFileByFileIdAndNotesId(@PathVariable Integer noteId,
                                                              @PathVariable Integer fileId)
            throws FileNotFoundException, FileNotesMismatchException {

        fileService.softDeleteFile(noteId, fileId);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "message", "File moved to trash successfully with fileId: " + fileId,
                "status", HttpStatus.OK
        ));

    }

    @GetMapping("/recycle-bin")
    public ResponseEntity<?> getRecycleBin() {

        // Fetch logged-in user ID
        // Hardcoded user ID for demonstration purposes
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"
                ));

        TrashResponse recycleBin = notesService.recycleBin(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", "Recycle bin data",
                        "data", recycleBin
                ));
    }

    @PutMapping("/recycle-bin/restore/{noteId}")
    public ResponseEntity<?> restoreNoteFromTrash(@PathVariable Integer noteId) {

        // Fetch logged-in user ID
        // Hardcoded user ID for demonstration purposes
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"
                ));

        RestoreNotesResponse restoreResponse = notesService.restoreNote(noteId, userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "restoredNoteId", restoreResponse.getRestoredNoteId(),
                        "message", "RESTORED successfully the data at:-" + restoreResponse.getRestoredAt(),
                        "status", HttpStatus.OK

                ));
    }

    @PutMapping("/recycle-bin/restore/{noteId}/file/{fileId}")
    public ResponseEntity<?> restoreFileFromTrash(@PathVariable Integer noteId,
                                                  @PathVariable Integer fileId) throws FileNotFoundException {

        // Fetch logged-in user ID
        // Hardcoded user ID for demonstration purposes
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"
                ));

        RestoreFileResponse restoreResponse = fileService.restoreFileResponse(fileId, noteId, userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "restoredFileId", restoreResponse.getRestoredFileId(),
                        "message", "RESTORED successfully the file with fileId:- " + restoreResponse.getRestoredFileId() +
                                " with associated noteId:- " + restoreResponse.getAssociatedNoteId() + " at " + restoreResponse.getRestoredAt(),
                        "status", HttpStatus.OK

                ));
    }


    @DeleteMapping("/recycle-bin/{noteId}/delete")
    public ResponseEntity<?> hardDeleteNotesById(@PathVariable Integer noteId) {

        // Fetch logged-in user ID
        // Hardcoded user ID for demonstration purposes
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"
                ));
        notesService.hardDeleteNotesById(noteId, userId);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "message", "Notes deleted permanently with ID: " + noteId,
                "status", HttpStatus.OK
        ));
    }


    @DeleteMapping("/recycle-bin/{noteId}/file/{fileId}/delete")
    public ResponseEntity<?> hardDeleteFileByFileId(@PathVariable Integer noteId, @PathVariable Integer fileId) {

        // Fetch logged-in user ID
        // Hardcoded user ID for demonstration purposes
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"
                ));

        fileService.hardDeleteFile(fileId, noteId, userId);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "message", "File deleted permanently with ID: " + fileId,
                "status", HttpStatus.OK
        ));

    }

    // testing remaining
    @DeleteMapping("/recycle-bin/empty")
    public ResponseEntity<?> emptyRecycleBin() {

        // Fetch logged-in user ID
        // Hardcoded user ID for demonstration purposes
        Integer userId = auditAwareConfig.getCurrentAuditor()
                .orElseThrow(() -> new UserNotFoundException(
                        "User is unauthenticated, please login with proper credentials"));

        notesService.emptyRecycleBin(userId);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "message", "Recycle bin emptied successfully!",
                "status", HttpStatus.OK
        ));

    }
}
