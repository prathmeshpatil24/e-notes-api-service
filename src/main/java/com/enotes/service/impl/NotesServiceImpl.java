package com.enotes.service.impl;


import com.enotes.dto.NotesListResponseModel;
import com.enotes.dto.NotesRequestModel;
import com.enotes.dto.PaginationResponse;
import com.enotes.entity.Category;
import com.enotes.entity.Notes;
import com.enotes.exceptions.InvalidPaginationParameterException;
import com.enotes.exceptions.SaveFailedException;
import com.enotes.repo.CategoryRepo;
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

@Service
public class NotesServiceImpl implements NotesService {

    @Autowired
    private NotesRepo notesRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private Validation validation;


    @Override
    public NotesRequestModel createNotes(NotesRequestModel notesRequestModel) {
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

        return notesRequestModel;

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

            Page<Notes> notePage = notesRepo.findByCreatedBy(userId, pageable);


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
}
