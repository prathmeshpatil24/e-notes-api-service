package com.enotes.controller;

import com.enotes.cofig.AuditAwareConfig;
import com.enotes.dto.FileDetailsResponse;
import com.enotes.dto.NotesListResponseModel;
import com.enotes.dto.NotesRequestModel;
import com.enotes.dto.PaginationResponse;
import com.enotes.entity.Notes;
import com.enotes.service.impl.FileServiceImpl;
import com.enotes.service.impl.NotesServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public ResponseEntity<?>createNewNotes(@RequestBody NotesRequestModel notesRequestModel){

        try {
            Notes notes = notesService.createNotes(notesRequestModel);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Notes created successfully with title: " + notes.getTitle(),
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


    @PutMapping("/update/{notesId}")
    public ResponseEntity<?> updateNotes(
            @PathVariable Integer notesId,
            @RequestBody NotesRequestModel notesRequestModel){
        try {
            Notes updateNotes = notesService.updateNotes(notesId, notesRequestModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of(
                            "message", "Notes updated successfully with ID: " + updateNotes.getId(),
                            "status", HttpStatus.CREATED
                    ));

        } catch (Exception e) {
            System.out.println("Error updating notes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Failed to update notes. ",
                    "status", HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }


    @PostMapping("/{notesId}/upload-files")
    public ResponseEntity<?> uploadFilesToNotes(
            @PathVariable Integer notesId,
            @RequestParam("files") List<MultipartFile> files){
        try {

            List<FileDetailsResponse> fileDetailsResponseList = fileService.uploadFiles(notesId, files);
            return ResponseEntity.status(HttpStatus.OK).body(
                    Map.of(
                            "message", "Files uploaded successfully to notes with ID: " + fileDetailsResponseList,
                            "status", HttpStatus.OK
                    ));
        } catch (Exception e) {
            System.out.println("Error uploading files to notes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Failed to upload files to notes. ",
                    "status", HttpStatus.INTERNAL_SERVER_ERROR
            ));

        }
        }


    @GetMapping("/{notesId}/view-file/{fileId}")
    public ResponseEntity<?> viewFile(@PathVariable Integer notesId,@PathVariable String fileName){
        try {
            var resource = fileService.downloadFile(notesId,fileName);
            String contentType = Files.probeContentType(resource.getFile().toPath());
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"").body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found: " + fileName);
        }
    }

    @GetMapping("/{notesId}/download-file/{fileName}")
    public ResponseEntity<?> downloadFile(@PathVariable Integer notesId,@PathVariable String fileName){
        try {
            var resource = fileService.downloadFile(notesId,fileName);
            String contentType = Files.probeContentType(resource.getFile().toPath());
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found: " + fileName);
        }
    }

    @DeleteMapping("{notesId}/move-to-trash")
    public ResponseEntity<?> softDeleteNotesById(@PathVariable Integer notesId){
        try {
            notesService.softDeleteNotesById(notesId);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "message", "Notes moved to trash successfully with ID: " + notesId,
                    "status", HttpStatus.OK
            ));
        } catch (Exception e) {
            System.out.println("Error soft deleting notes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Failed to move trash in trash with ID: " + notesId,
                    "status", HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }


    @DeleteMapping("/{notesId}/move-to-trash/file/{fileId}")
    public ResponseEntity<?> softDeleteFileByFileIdAndNotesId(@PathVariable Integer notesId,@PathVariable Integer fileId) {
        try {
            fileService.softDeleteFile(fileId, notesId);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "message", "File moved to trash successfully with fileId: " + fileId,
                    "status", HttpStatus.OK
            ));
        } catch (Exception e) {
            System.out.println("Error soft deleting file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Failed to move file in trash. ",
                    "status", HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @DeleteMapping("/recycle-bin/{notesId}/delete-permanently")
    public ResponseEntity<?>hardDeleteNotesById(@PathVariable Integer notesId){
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
    public ResponseEntity<?> hardDeleteFileByFileId(@PathVariable Integer fileId){
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
