package com.enotes.service.impl;

import com.enotes.dto.ActiveCategoryModel;
import com.enotes.dto.CategoryRequestModel;
import com.enotes.dto.CategoryResponseModel;
import com.enotes.entity.Category;
import com.enotes.exceptions.CategoryListException;
import com.enotes.exceptions.CategoryNotFoundException;
import com.enotes.exceptions.SaveFailedException;
import com.enotes.exceptions.ValidationException;
import com.enotes.repo.CategoryRepo;
import com.enotes.service.CategoryService;
import com.enotes.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private Validation validation;

    @Override
    public CategoryResponseModel saveCategory(CategoryRequestModel categoryRequestModel) {
        //validation
         validation.categoryValidation(categoryRequestModel);

         //check duplicate category name
        categoryRepo.findByName(categoryRequestModel.getName().trim())
                .ifPresent(category -> {
                    throw new DataIntegrityViolationException("Category with name '" + categoryRequestModel.getName() + "' already exists.");
                });


        Category category = new Category();
        //setting value from request model to entity
        category.setName(categoryRequestModel.getName().trim());
        category.setDescription(categoryRequestModel.getDescription().trim());
        category.setIsActive(true); // new category is active by default


        System.out.println(category.toString());
            try {
                Category savedCategory = categoryRepo.save(category);

                // Map entity -> response
                CategoryResponseModel response = new CategoryResponseModel();
                response.setId(savedCategory.getCategoryId());
                response.setName(savedCategory.getName());
                response.setDescription(savedCategory.getDescription());
                response.setActive(savedCategory.getIsActive());

                return response;

            } catch (DataIntegrityViolationException ex) {
                throw new SaveFailedException("Category save failed: duplicate or invalid data," +  ex);
            } catch (Exception ex) {
                throw new SaveFailedException("Unexpected error while saving new Category" + ex);
            }
    }

    @Override
    //this will give all category including Active and Inactive
    public List<CategoryResponseModel> getAllCategory() {
        List<Category> categories = categoryRepo.findAll();

        if (categories.isEmpty() ){
            throw new CategoryListException("No Categories found, category list is empty");
        }

        return categories.stream()
                .map(category -> {
            CategoryResponseModel categoryResponseModel = new CategoryResponseModel();

            categoryResponseModel.setId(category.getCategoryId());
            categoryResponseModel.setName(category.getName());
            categoryResponseModel.setDescription(category.getDescription());
            categoryResponseModel.setActive(category.getIsActive());
            categoryResponseModel.setCreatedBy(category.getCreatedBy());
            categoryResponseModel.setUpdatedBy(category.getUpdatedBy());
            categoryResponseModel.setCreatedAt(category.getCreatedAt());
            categoryResponseModel.setUpdatedAt(category.getUpdatedAt());

            return categoryResponseModel;
        }).toList();
    }

    @Override
    public List<ActiveCategoryModel> getOnlyActiveCategory() {
        List<Category> categories = categoryRepo.findByIsActiveTrue();

        if (categories.isEmpty()){
            throw new CategoryListException("No Categories found where isActive is True");

        }

        return categories.stream()
                .map(category -> {
                    ActiveCategoryModel activeCategoryModel = new ActiveCategoryModel();

                    activeCategoryModel.setId(category.getCategoryId());
                    activeCategoryModel.setName(category.getName());
                    activeCategoryModel.setDescription(category.getDescription());
                    activeCategoryModel.setActive(category.getIsActive());

                    return activeCategoryModel;
                }).toList();
    }

    @Override
    public CategoryResponseModel getCategoryById(Integer categoryId) {

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() ->  new CategoryNotFoundException("No category found with this id:- " + categoryId));

        try {
            CategoryResponseModel categoryResponseModel = new CategoryResponseModel();
            categoryResponseModel.setId(category.getCategoryId());
            categoryResponseModel.setName(category.getName());
            categoryResponseModel.setDescription(category.getDescription());
            categoryResponseModel.setActive(category.getIsActive());
            categoryResponseModel.setCreatedBy(category.getCreatedBy());
            categoryResponseModel.setUpdatedBy(category.getUpdatedBy());


            return categoryResponseModel;
        }catch (Exception exception){
            System.err.println("ERROR in mapping category: " + exception.getMessage());
            throw new RuntimeException("Unexpected internal error while processing category");

        }
    }

    @Override
    public Boolean disableCategoryById(Integer categoryId) {

        // Fetch category by ID (admin can disable any existing category)
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        // If already inactive → no need to disable again
        if (!category.getIsActive()) {
            throw new SaveFailedException("Category with id " + categoryId + " is already inactive.");
        }

        // Disable category
        category.setIsActive(false);

        try {
            categoryRepo.save(category);
            return true;

        } catch (DataIntegrityViolationException ex) {
            throw new SaveFailedException("Failed to update category due to invalid data.", ex);

        } catch (Exception ex) {
            throw new SaveFailedException("Unexpected error while disabling category.", ex);
        }
    }


    @Override
    public Boolean updateCategoryById(Integer categoryId, CategoryRequestModel categoryRequestModel) {

        //check validation and fetch existing category
        Category existingCategory = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));


        //update name
        if (categoryRequestModel.getName() != null) {

            String newCategoryName = categoryRequestModel.getName().trim();

            //check duplicate category name
            categoryRepo.findByName(newCategoryName)
                    .ifPresent(existing -> {
                        if (!existing.getCategoryId().equals(categoryId)) {
                            throw new DataIntegrityViolationException(
                                    "Category with name '" + newCategoryName + "' already exists."
                            );
                        }
                    });

            existingCategory.setName(newCategoryName);
        }

        // ----- Update Description -----
        if (categoryRequestModel.getDescription() != null) {
            existingCategory.setDescription(categoryRequestModel.getDescription().trim());
        }

        // ----- Update Active Status -----
        Boolean active = categoryRequestModel.getIsActive();
        if (active != null) {
            existingCategory.setIsActive(active);
        }else {
                throw new RuntimeException("isActive field is required and should be true or false");
        }


        //save updated category
        try {
            Category updateCategory = categoryRepo.save(existingCategory);
            return true;
        }catch (DataIntegrityViolationException ex) {
            throw new SaveFailedException("Category save failed: duplicate or invalid data", ex);
        }
        catch (Exception ex){
            throw new SaveFailedException("Unexpected error while updating category", ex);
        }
    }
}
