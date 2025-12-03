package com.enotes.controller;

import com.enotes.cofig.AuditAwareConfig;
import com.enotes.dto.*;
import com.enotes.entity.Notes;
import com.enotes.exceptions.FileHandlingException;
import com.enotes.exceptions.FileNotesMismatchException;
import com.enotes.exceptions.UserNotFoundException;
import com.enotes.service.impl.FileServiceImpl;
import com.enotes.service.impl.NotesServiceImpl;
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


    @PutMapping("/update/{notesId}")
    public ResponseEntity<?> updateNotes(
            @PathVariable Integer notesId,
            @RequestBody NotesRequestModel notesRequestModel) {

        Notes updateNotes = notesService.updateNotes(notesId, notesRequestModel);
        return ResponseEntity.status(HttpStatus.OK).body(
                Map.of(
                        "message", "Notes updated successfully with ID: " + updateNotes.getId(),
                        "status", HttpStatus.OK
                ));
    }


    @PostMapping("/{notesId}/upload-files")
    public ResponseEntity<?> uploadFilesToNotes(
            @PathVariable Integer notesId,
            @RequestParam("files") List<MultipartFile> files)
            throws FileHandlingException {

        List<FileDetailsResponse> fileDetailsResponseList = fileService.uploadFilesByNotesId(notesId, files);
        return ResponseEntity.status(HttpStatus.OK).body(
                Map.of(
                        "message", "Files uploaded successfully to notes Id:-" + notesId,
                        "status", HttpStatus.OK,
                        "files", fileDetailsResponseList
                ));
    }

    @GetMapping("/getDetails/{notesId}")
    public ResponseEntity<?> getFullNotesDetails(@PathVariable Integer notesId) {

        NotesFullDetailResponse fullDetailsByNotesId = notesService.getNotesFullDetailsByNotesId(notesId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "status", HttpStatus.OK.value(),
                        "data", fullDetailsByNotesId
                ));
    }


    @GetMapping("/getDetails/{notesId}/view-file/{fileName}")
    public ResponseEntity<?> viewFile(@PathVariable Integer notesId, @PathVariable String fileName) throws IOException {

        var resource = fileService.downloadFile(notesId, fileName);
            String contentType = Files.probeContentType(resource.getFile().toPath());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(resource);

    }

    @GetMapping("/getDetails/{notesId}/download-file/{fileName}")
    public ResponseEntity<?> downloadFile(@PathVariable Integer notesId, @PathVariable String fileName) throws IOException {

            var resource = fileService.downloadFile(notesId, fileName);
            String contentType = Files.probeContentType(resource.getFile().toPath());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);

    }

    @DeleteMapping("{notesId}/move-to-trash")
    public ResponseEntity<?> softDeleteNotesById(@PathVariable Integer notesId) {

            notesService.softDeleteNotesById(notesId);
            return ResponseEntity.status(HttpStatus.OK).
                    body(Map.of(
                    "message", "Notes moved to trash successfully with ID: " + notesId,
                    "status", HttpStatus.OK
            ));
    }

    // testing remaining
    @DeleteMapping("/{notesId}/move-to-trash/file/{fileId}")
    public ResponseEntity<?> softDeleteFileByFileIdAndNotesId(@PathVariable Integer notesId,
                                                              @PathVariable Integer fileId)
            throws FileNotFoundException,FileNotesMismatchException {

            fileService.softDeleteFile(notesId, fileId);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "message", "File moved to trash successfully with fileId: " + fileId,
                    "status", HttpStatus.OK
            ));

    }

    @DeleteMapping("/recycle-bin/{notesId}/delete-permanently")
    public ResponseEntity<?> hardDeleteNotesById(@PathVariable Integer notesId) {
        try {
            notesService.hardDeleteNotesById(notesId);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "message", "Notes deleted permanently with ID: " + notesId,
                    "status", HttpStatus.OK
            ));
        } catch (Exception e) {
            System.out.println("Error hard deleting notes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Failed to delete notes permanently with ID: " + notesId,
                    "status", HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @DeleteMapping("/recycle-bin/file/{fileId}/delete-permanently")
    public ResponseEntity<?> hardDeleteFileByFileId(@PathVariable Integer fileId) {
        try {
            fileService.hardDeleteFile(fileId);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "message", "File deleted permanently with ID: " + fileId,
                    "status", HttpStatus.OK
            ));
        } catch (Exception e) {
            System.out.println("Error hard deleting file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Failed to delete file permanently with ID: " + fileId,
                    "status", HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @DeleteMapping("/recycle-bin/empty")
    public ResponseEntity<?> emptyRecycleBin() {
        try {
            notesService.emptyRecycleBin();
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "message", "Recycle bin emptied successfully!",
                    "status", HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Error while emptying recycle bin",
                    "status", HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }
}
