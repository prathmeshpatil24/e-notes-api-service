package com.enotes.service.impl;

import com.enotes.cofig.AuditAwareConfig;
import com.enotes.dto.FileDetailsResponse;

import com.enotes.entity.FileEntity;
import com.enotes.entity.Notes;
import com.enotes.repo.FileRepo;
import com.enotes.repo.NotesRepo;
import com.enotes.service.FileService;
import com.enotes.utils.FileIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    public List<FileDetailsResponse> uploadFiles(Integer notesId, List<MultipartFile> files) throws IOException {
        //get notes by id
        Notes notes = notesRepo.findById(notesId).orElseThrow(() ->
                new RuntimeException("Notes not found with id: " + notesId)
        );

        //list to hold file details response
        List<FileDetailsResponse> fileDetailsResponselist = new ArrayList<>();

        for (MultipartFile file : files) {

            if (file.isEmpty()) continue; // skip empty files

            String filename = file.getOriginalFilename();
            System.out.println(" Original file name:- " + filename);

            //get the file name
            String username = String.valueOf(notes.getCreatedBy());

            Path path = Paths.get(fileUploadDir).resolve(username).normalize();

            if (!Files.exists(path)) {
                Files.createDirectories(path);
                System.out.println("Folder created for user: " + username);
            }

            //Full path to file = folder + file name
            assert filename != null;
            Path fullPath = path.resolve(filename);

            //copy file to the target location
            Files.copy(file.getInputStream(), fullPath, StandardCopyOption.REPLACE_EXISTING);

            //set the file name to notes entity
            FileEntity fileEntity = new FileEntity();

            //generate unique file name
            String saveFileName = fileIdGenerator.generateId(filename);
            fileEntity.setFileName(saveFileName);

            fileEntity.setFileSize((double) file.getSize());
            fileEntity.setFilePath(String.valueOf(fullPath));
            System.out.println("File saved at path: " + fullPath);

            //link notes with file
            fileEntity.setNotes(notes);

            FileEntity savedFile = fileRepo.save(fileEntity);
            System.out.println("File saved at path: " + savedFile.getFilePath());

            // Build response
            FileDetailsResponse fileDetailsResponseModel = new FileDetailsResponse();
            fileDetailsResponseModel.setNotesId(savedFile.getNotes().getId());
            fileDetailsResponseModel.setFileId(savedFile.getFileId());
            fileDetailsResponseModel.setFileName(savedFile.getFileName());
            fileDetailsResponseModel.setFileSize(savedFile.getFileSize());


            fileDetailsResponselist.add(fileDetailsResponseModel);
        }
        return fileDetailsResponselist;
    }

    @Override
    public Resource downloadFile(Integer notesId,String fileName) throws FileNotFoundException, IOException {
        // Step 1: Find file entry in DB

        FileEntity fileEntity = fileRepo.findByNotesIdAndFileName(notesId, fileName)
                .orElseThrow(() -> new FileNotFoundException("File not found in database or notesId: " + fileName + notesId));

//        FileEntity fileEntity = fileRepo.findByFileName(fileName)
//                .orElseThrow(() -> new FileNotFoundException("File not found in database: " + fileName));

        // Step 2: Get related info
        Notes notes = fileEntity.getNotes();
        Optional<Integer> currentAuditor = auditAwareConfig.getCurrentAuditor();

        Integer createdBy = currentAuditor.orElseThrow(() -> new RuntimeException("User not authenticated"));

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

    @Override
    public void deleteFile(String fileName) {

    }
}
