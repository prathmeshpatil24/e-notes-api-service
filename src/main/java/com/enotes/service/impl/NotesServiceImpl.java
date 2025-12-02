package com.enotes.service.impl;


import com.enotes.dto.*;
import com.enotes.entity.Category;
import com.enotes.entity.FileEntity;
import com.enotes.entity.Notes;
import com.enotes.exceptions.InvalidPaginationParameterException;
import com.enotes.exceptions.SaveFailedException;
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
    public Notes createNotes(NotesRequestModel notesRequestModel) {
        //validation
        validation.notesValidation(notesRequestModel);

        //check duplicate category name
        notesRepo.findByTitle(notesRequestModel.getNoteTitle().trim())
                .ifPresent(notes -> {
                    throw new DataIntegrityViolationException("Notes with title '" + notesRequestModel.getNoteTitle() + "' already exists.");
                });

        // fetch and set category into notes
        Category category = categoryRepo.findById(notesRequestModel.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + notesRequestModel.getCategoryId()));

        try {

        Notes notes = new Notes();
        //setting value from request model to entity
        notes.setTitle(notesRequestModel.getNoteTitle().trim());
        notes.setDescription(notesRequestModel.getNoteDescription().trim());
        notes.setCategory(category);

        Notes saved = notesRepo.save(notes);

        return saved;

        } catch (DataIntegrityViolationException ex) {
            throw new SaveFailedException("Notes save failed: duplicate or invalid data," +  ex);
        } catch (Exception ex) {
            throw new SaveFailedException("Unexpected error while saving new Category" + ex);
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
        if (!(sortDir.equalsIgnoreCase("asc") || sortDir.equalsIgnoreCase("desc"))) {
            throw new InvalidPaginationParameterException("Sort direction must be 'asc' or 'desc'");
        }
        try {
            //created sorting object
            Sort sort = sortDir.equalsIgnoreCase("asc")?
                    Sort.by(sortBy).ascending():
                    Sort.by(sortBy).descending();

            //created pageable object
            Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

            Page<Notes> notePage = notesRepo.findByCreatedByAndIsDeletedFalse(userId, pageable);


            //convert entity page to dto page
            Page<NotesListResponseModel> notesListResponseModelPage = notePage.map(
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

            return new PaginationResponse<>(notesListResponseModelPage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Notes updateNotes(Integer notesId, NotesRequestModel notesRequestModel) {

        //fetch existing notes
        Notes existingNotes = notesRepo.findByIdAndIsDeletedFalse(notesId)
                .orElseThrow(() -> new RuntimeException("Notes not found with ID: " + notesId));
        try{

        String notesTitle = notesRequestModel.getNoteTitle();
        String notesDescription = notesRequestModel.getNoteDescription();
        Integer categoryId = notesRequestModel.getCategoryId();

        if (notesTitle != null && !notesTitle.isBlank()){
            existingNotes.setTitle(notesTitle.trim());
        }
        if (notesDescription != null && !notesDescription.isBlank()){
            existingNotes.setDescription(notesDescription.trim());
        }
        if (categoryId != null ){
            // fetch and set category into notes
            Category category = categoryRepo.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found with ID: " + notesRequestModel.getCategoryId()));

            existingNotes.setCategory(category);
        }

        //updated notes
            return notesRepo.save(existingNotes);

        } catch (DataIntegrityViolationException ex) {
            throw new SaveFailedException("Notes update failed: duplicate or invalid data," +  ex);
        } catch (Exception ex) {
            throw new SaveFailedException("Unexpected error while updating updating the notes" + ex);
        }
    }

    @Override
    public NotesFullDetailResponse getNotesFullDetailsByNotesId(Integer notesId) {
        //fetch existing notes
        Notes existingNotes = notesRepo.findByIdAndIsDeletedFalse(notesId)
                .orElseThrow(() -> new RuntimeException("Notes not found with ID: " + notesId));

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
    }

    @Override
    public void softDeleteNotesById(Integer notesId) {

        Notes existingNotes = notesRepo.findByIdAndIsDeletedFalse(notesId)
                .orElseThrow(() ->
                    new RuntimeException("Notes not found with id:- " + notesId)
        );

        try {
            // Soft delete associated files
            existingNotes.getFileEntity().forEach(
                    file -> file.setDeleted(true)
            );
            // Soft delete notes
            existingNotes.setDeleted(true);

            // Save the changes
            notesRepo.save(existingNotes);

        } catch (Exception e) {
            System.out.println("Error:- " + e.getMessage());
            throw new RuntimeException("Error occurred while moving to recycle bin notes with id:- " + notesId);
        }
    }

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
                        if (f.exists()) f.delete();

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
