package com.enotes.service.impl;

import com.enotes.cofig.AuditAwareConfig;
import com.enotes.dto.FileDetailsResponse;

import com.enotes.dto.RestoreFileResponse;
import com.enotes.entity.FileEntity;
import com.enotes.entity.Notes;
import com.enotes.exceptions.*;
import com.enotes.repo.FileRepo;
import com.enotes.repo.NotesRepo;
import com.enotes.service.FileService;
import com.enotes.utils.FileIdGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileRepo fileRepo;

    @Autowired
    private NotesRepo notesRepo;

    @Autowired
    private AuditAwareConfig auditAwareConfig;

    @Autowired
    private FileIdGenerator fileIdGenerator;

    @Value("${file.upload-dir.files}")
    private String fileUploadDir;

    @Override
    public List<FileDetailsResponse> uploadFilesByNotesId(Integer notesId, List<MultipartFile> files) throws FileHandlingException {

        //get notes by id
        Notes notes = notesRepo.findById(notesId).
                orElseThrow(() -> new NotesNotFoundException(notesId));

      try {
          //list to hold file details response
          List<FileDetailsResponse> fileDetailsResponselist = new ArrayList<>();

          for (MultipartFile file : files) {

              if (file.isEmpty()){ continue;} // skip empty files

              String originalFilename = file.getOriginalFilename();
              System.out.println("Original File name:- " + originalFilename);

              //generate unique file name
//              String displayFileName = fileIdGenerator.generateId(originalFilename);
//              System.out.println("Display File name:- " + displayFileName);

              //get the file name
              String username = String.valueOf(notes.getCreatedBy());

              Path path = Paths.get(fileUploadDir).resolve(username).normalize();

              if (!Files.exists(path)) {
                  Files.createDirectories(path);
                  System.out.println("Folder created for user: " + username);
              }

              //Full path to file = folder + file name
              assert originalFilename != null;
              Path fullPath = path.resolve(originalFilename);// creating file path by display file name

              //copy file to the target location
              Files.copy(file.getInputStream(), fullPath, StandardCopyOption.REPLACE_EXISTING);

              //set the file name to notes entity
              FileEntity fileEntity = new FileEntity();
              fileEntity.setFileName(originalFilename);
              fileEntity.setFileSize((double) file.getSize());
              fileEntity.setFilePath(String.valueOf(fullPath));
              System.out.println("File saved at path: " + fullPath);
              //link notes with file
              fileEntity.setNotes(notes);

              //saving all details in db
              FileEntity savedFile = fileRepo.save(fileEntity);
              System.out.println("File saved at path: " + savedFile.getFilePath());

              // Build response
              FileDetailsResponse response = new FileDetailsResponse();
              response.setNotesId(savedFile.getNotes().getId());
              response.setFileId(savedFile.getFileId());
              response.setFileName(savedFile.getFileName());
              response.setFileSize(savedFile.getFileSize());
              response.setCreatedAt(savedFile.getCreatedAt());
              response.setCreatedBy(savedFile.getCreatedBy());

              //adding all notes files for response
              fileDetailsResponselist.add(response);
          }

          return fileDetailsResponselist;

      } catch (IOException ioException){
         throw new FileHandlingException("Unexpected error while uploading file.", ioException);
      }
    }

    @Override
    public Resource downloadFile(Integer notesId,String fileName) throws IOException {
        // Step 1: Find file entry in DB

        FileEntity fileEntity = fileRepo.findByNotesIdAndFileNameAndIsDeletedFalse(notesId, fileName)
                .orElseThrow(() -> new FileNotFoundException("File not found in database with notesId: " + notesId + "and file name:- " + fileName));

//        FileEntity fileEntity = fileRepo.findByFileName(fileName)
//                .orElseThrow(() -> new FileNotFoundException("File not found in database: " + fileName));

        // Step 2: Get related info
        Notes notes = fileEntity.getNotes();
        Optional<Integer> currentAuditor = auditAwareConfig.getCurrentAuditor();

        Integer createdBy = currentAuditor.orElseThrow(() -> new UserNotFoundException("User not authenticated"));

        // Step 3: Build full file path -> uploadsFiles/{createdBy}/{fileName}
        Path path = Paths.get(fileUploadDir).resolve(String.valueOf(createdBy)).resolve(fileName).normalize();

        System.out.println("Resolved path for download: " + path.toAbsolutePath());

        // Step 4: Create resource
        Resource resource = new UrlResource(path.toUri());
        if (resource.exists()) {
            return resource;
        } else {
            throw new FileNotFoundException("File not found at path: " + path);
        }
    }

    @Transactional
    @Override
    public void softDeleteFile(Integer notesId, Integer fileId)
            throws FileNotFoundException, FileNotesMismatchException {
        //check fileId is valid or not
        FileEntity existingFileEntity = fileRepo.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("Invalid file ID: " + fileId));

        //match with notesId
        if (!existingFileEntity.getNotes().getId().equals(notesId)) {
            System.out.println("fileId and notesId Mismatched");
            throw new FileNotesMismatchException(
                    "File with ID " + fileId + " does not belong to notes ID " + notesId
            );
        }

//        FileEntity existingFileEntity = fileRepo.findByFileIdAndNotesIdAndIsDeletedFalse(fileId,notesId)
//                .orElseThrow(() -> new RuntimeException("File not found with id:- " + fileId));
        try {
            // Soft delete file
            existingFileEntity.setIsDeleted(true);
            existingFileEntity.setDeletedAt(LocalDateTime.now());

            // Save the changes
            fileRepo.save(existingFileEntity);
        }
        catch (Exception e) {
            System.out.println("Error:- " + e.getMessage());
            throw new SoftDeleteFailedException("Error occurred while moving to recycle bin file with id:- " + fileId + " " + e.getMessage());
        }
    }

    @Override
    public RestoreFileResponse restoreFileResponse(Integer fileId,
                                                   Integer noteId,
                                                   Integer userId)
            throws FileNotFoundException {


            FileEntity file = fileRepo.findByFileIdAndNotes_IdAndCreatedByAndIsDeletedTrue(fileId, noteId, userId)
                    .orElseThrow(() -> new FileNotFoundException(
                            "No deleted file found for fileId=" + fileId +
                                    ", noteId=" + noteId +
                                    ", userId=" + userId
                    ));

            // Restore file
            file.setIsDeleted(false);
            file.setDeletedAt(null);

            try {
                FileEntity restoredFile = fileRepo.save(file);

                RestoreFileResponse response = new RestoreFileResponse();
                response.setRestoredFileId(restoredFile.getFileId());
                response.setAssociatedNoteId(restoredFile.getNotes().getId());
                response.setMessage("File restored successfully");
                response.setRestoredAt(LocalDateTime.now());

                return response;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Unexpected error" , e);
            }
    }

    //testing remaining
    @Transactional
    @Override
    public void hardDeleteFile(Integer fileId,
                               Integer noteId,
                               Integer createdBy) {

        FileEntity existingFileEntity = fileRepo.findByFileIdAndNotes_IdAndCreatedByAndIsDeletedTrue(fileId,
                        noteId,
                        createdBy)
                .orElseThrow(() ->
                        new UserNotesIdException( "No deleted file found for fileId: " + fileId +
                                ", noteId: " + noteId +
                                ", userId: " + createdBy));
        try {
            // Delete actual file from disk
            File f = new File(existingFileEntity.getFilePath());

            if (f.exists()){
                    f.delete();
            }

            // Delete DB record
            fileRepo.delete(existingFileEntity);

        } catch (Exception e) {
           e.printStackTrace();
            throw new RuntimeException("Error occurred while hard deleting file with id:- " + fileId);
        }

    }

}
