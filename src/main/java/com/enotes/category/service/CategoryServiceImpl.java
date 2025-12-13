package com.enotes.category.service;

import com.enotes.dto.ActiveCategoryModel;
import com.enotes.dto.CategoryRequestModel;
import com.enotes.dto.CategoryResponseModel;
import com.enotes.category.entity.Category;
import com.enotes.exceptions.CategoryListException;
import com.enotes.exceptions.CategoryNotFoundException;
import com.enotes.exceptions.SaveFailedException;
import com.enotes.category.repository.CategoryRepo;
import com.enotes.utils.Validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private Validation validation;

    @Override
    public CategoryResponseModel saveCategory(CategoryRequestModel request) {
        //validation
         validation.categoryValidation(request);

         //check duplicate category name
        categoryRepo.findByName(request.getName().trim())
                .ifPresent(category -> {
                    throw new DataIntegrityViolationException("Category with name '" + request.getName() + "' already exists.");
                });


        Category category = new Category();
        //setting value from request model to entity
        category.setName(request.getName().trim());
        category.setDescription(request.getDescription().trim());
        category.setIsActive(true); // new category is active by default

            try {
                Category savedCategory = categoryRepo.save(category);

                // Map entity -> response
                CategoryResponseModel response = new CategoryResponseModel();
                response.setId(savedCategory.getId());
                response.setName(savedCategory.getName());
                response.setDescription(savedCategory.getDescription());
                response.setIsActive(savedCategory.getIsActive());

                return response;

            }catch (DataIntegrityViolationException ex) {
                throw new SaveFailedException("Category save failed: duplicate or invalid data." ,  ex);
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

            categoryResponseModel.setId(category.getId());
            categoryResponseModel.setName(category.getName());
            categoryResponseModel.setDescription(category.getDescription());
            categoryResponseModel.setIsActive(category.getIsActive());
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
            throw new CategoryListException("No Categories found where Active status is True");
        }

        return categories.stream()
                .map(category -> {
                    ActiveCategoryModel activeCategoryModel = new ActiveCategoryModel();

                    activeCategoryModel.setId(category.getId());
                    activeCategoryModel.setName(category.getName());
                    activeCategoryModel.setDescription(category.getDescription());
                    activeCategoryModel.setIsActive(category.getIsActive());

                    return activeCategoryModel;
                }).toList();
    }

    @Override
    public List<ActiveCategoryModel> getOnlyInActiveCategory() {
        List<Category> categories = categoryRepo.findByIsActiveFalse();

        if (categories.isEmpty()){
            throw new CategoryListException("No Categories found where Active status is False");
        }

        return categories.stream()
                .map(category -> {
                    ActiveCategoryModel inActive = new ActiveCategoryModel();

                    inActive.setId(category.getId());
                    inActive.setName(category.getName());
                    inActive.setDescription(category.getDescription());
                    inActive.setIsActive(category.getIsActive());

                    return inActive;
                }).toList();
    }

    @Override
    public CategoryResponseModel getCategoryById(Integer categoryId) {

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() ->  new CategoryNotFoundException(categoryId));

            CategoryResponseModel categoryResponseModel = new CategoryResponseModel();

            categoryResponseModel.setId(category.getId());
            categoryResponseModel.setName(category.getName());
            categoryResponseModel.setDescription(category.getDescription());
            categoryResponseModel.setIsActive(category.getIsActive());
            categoryResponseModel.setCreatedBy(category.getCreatedBy());
            categoryResponseModel.setUpdatedBy(category.getUpdatedBy());
            categoryResponseModel.setUpdatedAt(category.getUpdatedAt());
            categoryResponseModel.setCreatedAt(category.getCreatedAt());

            return categoryResponseModel;
        }

    @Override
    public void disableCategoryById(Integer categoryId) {

        // Fetch category by ID (admin can disable any existing category)
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        // If already inactive → no need to disable again
        if (!category.getIsActive()) {
            throw new SaveFailedException("Category with id " + categoryId + " is already inactive.");
        }

        try {
        // Disable category
        category.setIsActive(false);

        categoryRepo.save(category);

        } catch (Exception ex) {
            throw new SaveFailedException("error while disabling category.", ex);
        }
    }

    @Override
    public CategoryResponseModel updateCategoryById(Integer categoryId, CategoryRequestModel request) {

        //check validation and fetch existing category
        Category existingCategory = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));


        validation.categoryValidation(request);

        //update name
        if (request.getName() != null) {

            String newCategoryName = request.getName().trim();

            //check duplicate category name
            categoryRepo.findByName(newCategoryName)
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(categoryId)) {
                            throw new DataIntegrityViolationException(
                                    "Category with name '" + newCategoryName + "' already exists."
                            );
                        }
                    });

            existingCategory.setName(newCategoryName);
        }

        existingCategory.setDescription(request.getDescription().trim());

        // ----- Update Active Status -----
        Boolean active = request.getIsActive();
        if (active != null) {
            existingCategory.setIsActive(active);
        }

        //save updated category
        try {
            Category savedCategory = categoryRepo.save(existingCategory);

            CategoryResponseModel response = new CategoryResponseModel();
            response.setId(savedCategory.getId());
            response.setName(savedCategory.getName());
            response.setDescription(savedCategory.getDescription());
            response.setIsActive(savedCategory.getIsActive());

            return response;
        }
        catch (Exception ex){
            throw new SaveFailedException("error while updating category", ex);
        }
    }
}
