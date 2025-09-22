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
        category.setName(categoryRequestModel.getName());
        category.setDescription(categoryRequestModel.getDescription());
        category.setIsActive(categoryRequestModel.getIsActive());

        //setting default value
        category.setIsDeleted(false);//not deleted
        category.setCreatedBy(2);//Admin
        category.setCreatedAt(new Date());

        System.out.println(category.toString());
            try {
                Category savedCategory = categoryRepo.save(category);

                // Map entity -> response
                CategoryResponseModel response = new CategoryResponseModel();
                response.setId(savedCategory.getCategoryId());
                response.setName(savedCategory.getName());
                response.setDescription(savedCategory.getDescription());
                response.setActive(savedCategory.getIsActive());
                response.setDeleted(savedCategory.getIsDeleted());
                response.setCreatedBy(savedCategory.getCreatedBy());
                response.setCreatedAt(savedCategory.getCreatedAt());

                return response;

            } catch (DataIntegrityViolationException ex) {
                throw new SaveFailedException("Category save failed: duplicate or invalid data," +  ex);
            } catch (Exception ex) {
                throw new SaveFailedException("Unexpected error while saving new Category" + ex);
            }
    }

    @Override
    //this will give all category including deleted and not deleted
    public List<CategoryResponseModel> getAllCategory() {
        List<Category> categories = categoryRepo.findAll();

        if (categories.isEmpty() ){
            throw new CategoryListException("No Categories found, category list is empty");

        }

        List<CategoryResponseModel> categoryResponseModels = categories.stream()
                .map(category -> {
            CategoryResponseModel categoryResponseModel = new CategoryResponseModel();

            categoryResponseModel.setId(category.getCategoryId());
            categoryResponseModel.setName(category.getName());
            categoryResponseModel.setDescription(category.getDescription());
            categoryResponseModel.setActive(category.getIsActive());
            categoryResponseModel.setDeleted(category.getIsDeleted());
            categoryResponseModel.setCreatedBy(category.getCreatedBy());
            categoryResponseModel.setUpdatedBy(category.getUpdatedBy());
            categoryResponseModel.setCreatedAt(category.getCreatedAt());
            categoryResponseModel.setUpdatedAt(category.getUpdatedAt());

            return categoryResponseModel;
        }).toList();
        return categoryResponseModels;
    }

    @Override
    public List<ActiveCategoryModel> getOnlyActiveCategory() {
        List<Category> categories = categoryRepo.findByIsActiveTrueAndIsDeletedFalse();

        if (categories.isEmpty()){
            throw new CategoryListException("No Categories found where isActive is True and isDeleted False, category list is empty for this condition");

        }

        List<ActiveCategoryModel> activeCategoryModelList = categories.stream()
                .map(category -> {
                    ActiveCategoryModel activeCategoryModel = new ActiveCategoryModel();

                    activeCategoryModel.setId(category.getCategoryId());
                    activeCategoryModel.setName(category.getName());
                    activeCategoryModel.setDescription(category.getDescription());
                    activeCategoryModel.setActive(category.getIsActive());

                    return activeCategoryModel;
                }).toList();
        return activeCategoryModelList;
    }

    @Override
    public CategoryResponseModel getCategoryById(Integer categoryId) {
        Category category = categoryRepo.findByIdAndIsDeletedFalse(categoryId)
                .orElseThrow(() ->  new CategoryNotFoundException(categoryId));

        CategoryResponseModel categoryResponseModel = new CategoryResponseModel();
        categoryResponseModel.setId(category.getCategoryId());
        categoryResponseModel.setName(category.getName());
        categoryResponseModel.setDescription(category.getDescription());
        categoryResponseModel.setActive(category.getIsActive());
        categoryResponseModel.setDeleted(category.getIsDeleted());
        categoryResponseModel.setCreatedBy(category.getCreatedBy());
        categoryResponseModel.setUpdatedBy(category.getUpdatedBy());
        categoryResponseModel.setCreatedAt(category.getCreatedAt());
        categoryResponseModel.setUpdatedAt(category.getUpdatedAt());

        return categoryResponseModel;
    }

    @Override
    public Boolean deleteCategoryById(Integer categoryId) {
        Category existingCategory = categoryRepo.findByIdAndIsActiveTrueAndIsDeletedFalse(categoryId)
                .orElseThrow(() ->  new CategoryNotFoundException("category is inactive or already deleted with id:- " + categoryId));

        existingCategory.setIsDeleted(true);
        existingCategory.setIsActive(false);
        try {
            Category savedCategory = categoryRepo.save(existingCategory);

            if (savedCategory == null || savedCategory.getCategoryId() == null) {
                throw new SaveFailedException(" For soft delete Category could not be saved due to unknown error");
            }
            return true;
        } catch (DataIntegrityViolationException ex) {
            throw new SaveFailedException("For soft delete Category save failed: duplicate or invalid data", ex);
        } catch (Exception ex) {
            throw new SaveFailedException("Unexpected error while Soft deleting  category", ex);
        }
    }

    @Override
    public Boolean updateCategoryById(Integer categoryId, CategoryRequestModel categoryRequestModel) {
        //check validation
        Category existingCategory = categoryRepo.findByIdAndIsActiveTrueAndIsDeletedFalse(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));


        if (categoryRequestModel.getName() != null) {
            //check duplicate category name
            categoryRepo.findByName(categoryRequestModel.getName().trim())
                    .ifPresent(category -> {
                        throw new DataIntegrityViolationException("Category with name '" + categoryRequestModel.getName() + "' already exists.");
                    });
            existingCategory.setName(categoryRequestModel.getName());
        }
        if (categoryRequestModel.getDescription() != null) {
            existingCategory.setDescription(categoryRequestModel.getDescription());
        }
        if (categoryRequestModel.getIsActive()== true || categoryRequestModel.getIsActive() == false) {
            existingCategory.setIsActive(categoryRequestModel.getIsActive());
        }else {
            throw new RuntimeException("isActive field is required and should be true or false");
        }

        //check duplicate category name
        categoryRepo.findByName(categoryRequestModel.getName().trim())
                .ifPresent(category -> {
                    throw new DataIntegrityViolationException("Category with name '" + categoryRequestModel.getName() + "' already exists.");
                });

        //setting new value from request model to entity after validation
        existingCategory.setName(categoryRequestModel.getName());
        existingCategory.setDescription(categoryRequestModel.getDescription());
        existingCategory.setIsActive(categoryRequestModel.getIsActive());

        //setting default value
        existingCategory.setUpdatedBy(2);//admin
        existingCategory.setUpdatedAt(new Date());

        try {
            Category updateCategory = categoryRepo.save(existingCategory);
            return true;
        }catch (DataIntegrityViolationException ex) {
            throw new SaveFailedException("Category save failed: duplicate or invalid data" + ex);
        }
        catch (Exception ex){
            throw new SaveFailedException("Unexpected error while updating category" + ex);
        }
    }
}
