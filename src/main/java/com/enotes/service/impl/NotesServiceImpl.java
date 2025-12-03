package com.enotes.service.impl;


import com.enotes.dto.*;
import com.enotes.entity.Category;
import com.enotes.entity.FileEntity;
import com.enotes.entity.Notes;
import com.enotes.exceptions.*;
import com.enotes.repo.CategoryRepo;
import com.enotes.repo.FileRepo;
import com.enotes.repo.NotesRepo;
import com.enotes.service.NotesService;
import com.enotes.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotesServiceImpl implements NotesService {

    @Autowired
    private NotesRepo notesRepo;

    @Autowired
    private FileRepo fileRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private Validation validation;


    @Override
    public Notes createNotes(NotesRequestModel request) {

        //validation
        validation.notesValidation(request);

        //check duplicate category name
        String noteTitle = request.getNoteTitle().trim();
        notesRepo.findByTitle(noteTitle)
                .ifPresent(notes -> {
                    throw new DataIntegrityViolationException("Notes with title '" + noteTitle + "' already exists.");
                });

        // fetch and set category into notes
        Category category = categoryRepo.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));

        //chack category is acctive or not if category is inactive then show proper message for it
        // 2. Validate category status
        if (!category.getIsActive()) {
            throw new InActiveCategoryException("Cannot create notes under an inactive categoryId: " + category.getId() + ", category name:- " + category.getName());
        }

        Notes notes = new Notes();
        //setting value from request model to entity
        notes.setTitle(request.getNoteTitle().trim());
        notes.setDescription(request.getNoteDescription().trim());
        notes.setCategory(category);

        try {

            return notesRepo.save(notes);

        } catch (Exception ex) {
            throw new SaveFailedException("Notes save failed:" +  ex);

        }
    }

    // short notes title list showing
    @Override
    public PaginationResponse<NotesListResponseModel> getNotesTitleList(Integer userId,
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
            Sort sort = sortDir.equalsIgnoreCase("asc")?
                    Sort.by(sortBy).ascending():
                    Sort.by(sortBy).descending();

            //created pageable object
            Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

            //notes page details
            Page<Notes> notePages = notesRepo.findByCreatedByAndIsDeletedFalse(userId, pageable);

            //validate page number does not exceed total pages
            int totalPages = notePages.getTotalPages();
            if (totalPages > 0 && pageNo >= totalPages) {
                throw new InvalidPaginationParameterException(
                        "Page number " + pageNo + " exceeds the maximum available pages: " + (totalPages - 1)
                );
            }

            //convert entity page to dto page
            Page<NotesListResponseModel> notesListResponseModelPage = notePages.map(
                    note ->
                    {
                        NotesListResponseModel dto = new NotesListResponseModel();
                        dto.setId(note.getId());
                        dto.setTitle(note.getTitle());
                        dto.setDescription(note.getDescription());
                        dto.setCategoryName(note.getCategory().getName());
                        dto.setCreatedAt(note.getCreatedAt());
                        dto.setUpdateAt(note.getUpdatedAt());
                        return dto;
                    });
            //further add file counts also

            return new PaginationResponse<>(notesListResponseModelPage);
        } catch (Exception ex) {
            throw new NotesListFetchException("Failed to fetch notes list. Reason: " + ex.getMessage());
        }

    }

    @Override
    public Notes updateNotes(Integer notesId, NotesRequestModel request) {

        //fetch existing notes
        Notes existingNotes = notesRepo.findByIdAndIsDeletedFalse(notesId)
                .orElseThrow(() -> new NotesNotFoundException(notesId));

        try{
            // title
            if (request.getNoteTitle() != null) {
                String title = request.getNoteTitle().trim();
                 if (title.length()<2 || title.length()>100){
                     throw new InvaildInputData("title length should be in between 2 to 100");
                 }
                    existingNotes.setTitle(title);
            }

            // description
            if (request.getNoteDescription() != null) {
                String description = request.getNoteDescription().trim();
                if (description.length()<10 || description.length()>500){
                    throw new InvaildInputData("description length should be in between 10 to 500");
                }
                    existingNotes.setDescription(description);
            }
        if (request.getCategoryId() != null ){
            // fetch and set category into notes
            Category category = categoryRepo.findById(request.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));
            existingNotes.setCategory(category);
        }
             //updated notes
            return notesRepo.save(existingNotes);

        } catch (InvaildInputData | CategoryNotFoundException ex) {
            throw new RuntimeException(ex.getMessage());
        } catch (Exception ex) {
            throw new SaveFailedException("Notes update failed: duplicate or invalid data," +  ex);
        }
    }

    @Override
    public NotesFullDetailResponse getNotesFullDetailsByNotesId(Integer notesId) {
        //fetch existing notes
        Notes existingNotes = notesRepo.findByIdAndIsDeletedFalse(notesId)
                .orElseThrow(() -> new NotesNotFoundException(notesId));

       try {
           //map entity to dto
           NotesFullDetailResponse dto = new NotesFullDetailResponse();
           dto.setNotesId(existingNotes.getId());
           dto.setTitle(existingNotes.getTitle());
           dto.setDescription(existingNotes.getDescription());
           dto.setCategoryName(existingNotes.getCategory().getName());
           dto.setCreatedAt(existingNotes.getCreatedAt());
           dto.setUpdateAt(existingNotes.getUpdatedAt());

           //map file details
           if (existingNotes.getFileEntity() != null && !existingNotes.getFileEntity().isEmpty()) {

               List<FileDetailsResponse> fileDetailsResponses = existingNotes.getFileEntity()
                       .stream()
                       .map(
                               fileEntity -> {
                                   FileDetailsResponse fileDto = new FileDetailsResponse();
                                   fileDto.setFileId(fileEntity.getFileId());
                                   fileDto.setFileName(fileEntity.getFileName());
                                   fileDto.setFileSize(fileEntity.getFileSize());
                                   return fileDto;
                               }).collect(Collectors.toList());
               dto.setFiles(fileDetailsResponses);
           }
           return dto;
       } catch (Exception e) {
           e.printStackTrace();
           throw new RuntimeException("Unexpected error" , e);
       }
    }

    @Override
    public void softDeleteNotesById(Integer notesId) {

        // is deleted should be false for moving to recycle bean
        Notes existingNotes = notesRepo.findByIdAndIsDeletedFalse(notesId)
                .orElseThrow(() ->
                    new NotesNotFoundException(notesId));

        try {

            // Soft delete associated files
            existingNotes.getFileEntity()
                    .forEach(file -> {
                        file.setIsDeleted(true);
                        file.setDeletedAt(LocalDateTime.now());
                    });

            // Soft delete notes
            existingNotes.setIsDeleted(true);
            existingNotes.setDeletedAt(LocalDateTime.now());

            // Save the changes
            notesRepo.save(existingNotes);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to move notes with id " + notesId + " to recycle bin.");
            throw new SoftDeleteFailedException("Failed to move notes with id " + notesId + " to recycle bin." + " " + e.getMessage());
        }
    }

    //testing remaining
    @Override
    public void hardDeleteNotesById(Integer notesId) {
        Notes existingNotes = notesRepo.findByIdAndIsDeletedTrue(notesId).orElseThrow(
                () -> new RuntimeException("Notes not found with id:- " + notesId)
        );

        try{
            // Delete files from disk and DB
            existingNotes.getFileEntity().forEach(
                    file ->{
                        // delete associated files from db and storage also
                        // Delete actual file from disk
                        File f = new File(file.getFilePath());
                        if (f.exists()) {
                            f.delete();

                        }
                        // Delete DB record
                        fileRepo.delete(file);
                    }
            );

            // Delete notes from DB
            notesRepo.delete(existingNotes);

        } catch (RuntimeException e) {
            System.out.println("Error:- " + e.getMessage());
            throw new RuntimeException("Error occurred while deleting notes with id:- " + notesId);
        }
    }

    @Override
    public void emptyRecycleBin() {

        // 1. Delete all soft-deleted FILES (including files from soft-deleted notes)
        List<FileEntity> deletedFiles = fileRepo.findAllByIsDeletedTrue();

        for (FileEntity file : deletedFiles) {
            // Delete file from disk
            File physicalFile = new File(file.getFilePath());
            if (physicalFile.exists()) physicalFile.delete();

            // Delete DB record
            fileRepo.delete(file);
        }

        // 2. Delete all soft-deleted NOTES
        List<Notes> deletedNotes = notesRepo.findAllByIsDeletedTrue();

        for (Notes note : deletedNotes) {
            // Optional: clear file list from note before deleting
            note.getFileEntity().clear();
            notesRepo.delete(note);
        }
    }
}
