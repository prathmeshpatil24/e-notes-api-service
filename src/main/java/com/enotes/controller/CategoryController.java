package com.enotes.controller;

import com.enotes.dto.ActiveCategoryModel;
import com.enotes.dto.CategoryRequestModel;
import com.enotes.dto.CategoryResponseModel;
import com.enotes.entity.Category;
import com.enotes.exceptions.CategoryListException;
import com.enotes.exceptions.CategoryNotFoundException;
import com.enotes.exceptions.SaveFailedException;
import com.enotes.exceptions.ValidationException;
import com.enotes.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<?> saveCategory(@RequestBody CategoryRequestModel categoryRequestModel) {
        try {
            CategoryResponseModel saveCategory = categoryService.saveCategory(categoryRequestModel);
            if (saveCategory!=null) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of(
                                "message", "new category saved with id:- " + saveCategory.getId() + " name:- " + saveCategory.getName(),
                                "status", HttpStatus.CREATED
                        ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "message", "Failed to save category",
                                "status", HttpStatus.BAD_REQUEST
                        ));
            }
        } catch (DataIntegrityViolationException ex) {
            System.out.println("DataIntegrityViolationException while getting all category: " + ex.getMessage());

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message", ex.getMessage(),
                            "status", HttpStatus.CONFLICT
                    ));
        } catch (ValidationException validationException) {
            System.out.println("ValidationException while creating new category: " + validationException.getMessage());

//            List<Object> errorDetails = validationException.getErrors()
//                    .entrySet()
//                    .stream()
//                    .map(entry -> entry.getValue())   // ONLY VALUES
//                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", validationException.getMessage(),
                            "errorDetails", validationException.getErrors(),
                            "status", HttpStatus.BAD_REQUEST
                    ));
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            System.out.println("Exception while creating new category: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", e.getMessage(),
                            "status", HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllCategory() {

        try {

            List<CategoryResponseModel> allCategory = categoryService.getAllCategory();

            if (CollectionUtils.isEmpty(allCategory)) {

                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                        Map.of(
                                "message", "No category found",
                                "status", HttpStatus.NO_CONTENT
                        ));
            } else {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of(
                                "message", "All categories retrieved successfully",
                                "status", HttpStatus.OK,
                                "data", allCategory
                        ));
            }
        } catch (RuntimeException e) {
            System.out.println("Exception while getting all category: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", e.getMessage(),
                            "status", HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @GetMapping("/only-active")
    public ResponseEntity<?> getOnlyActiveCategory() {

        try {
            List<ActiveCategoryModel> onlyActiveCategory = categoryService.getOnlyActiveCategory();

            if (CollectionUtils.isEmpty(onlyActiveCategory)) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(Map.of(
                                "message", "No active categories found",
                                "status", HttpStatus.NO_CONTENT
                        ));
            } else {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of(
                                "message", "Active categories retrieved successfully",
                                "status", HttpStatus.OK,
                                "data", onlyActiveCategory
                        ));
            }

        }  catch (RuntimeException e) {
            System.out.println("Exception while getting only active category: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", e.getMessage(),
                            "status", HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Integer id) {

        try {
            CategoryResponseModel category = categoryService.getCategoryById(id);

                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of(
                        "message", "Category found with this id:- " + id,
                        "status", HttpStatus.OK,
                        "data", category
                ));
        } catch (CategoryNotFoundException ex){
            System.out.println("CategoryNotFoundException while getting the category by id: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message", ex.getMessage(),
                            "status", HttpStatus.NOT_FOUND
                    ));
        } catch (Exception e) {
            System.out.println("Exception while getting the category by id: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", e.getMessage(),
                            "status", HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> disableCategoryById(@PathVariable Integer id) {

        try {
            Boolean deleteCategoryById = categoryService.disableCategoryById(id);

            if (deleteCategoryById) {
                System.out.println("deleteCategoryById:- " + deleteCategoryById);

                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of(
                        "message", "Category disabled with this id:- " + id,
                        "status", HttpStatus.OK
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                        "message", "No category found with this id:- " + id,
                        "status", HttpStatus.NOT_FOUND
                        ));
            }
        } catch (CategoryNotFoundException ex){
            System.out.println("CategoryNotFoundException while getting the category by id: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message", ex.getMessage(),
                            "status", HttpStatus.NOT_FOUND
                    ));
        }catch (SaveFailedException ex){
            System.out.println("SaveFailedException while disabling the category: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", ex.getMessage(),
                            "status", HttpStatus.BAD_REQUEST
                    ));
        }
        catch (RuntimeException e) {
            System.out.println("Exception while disabling the category " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", e.getMessage(),
                            "status", HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCategoryById(@PathVariable Integer id, @RequestBody CategoryRequestModel categoryRequestModel) {

        try {
            Boolean updateCategoryById = categoryService.updateCategoryById(id, categoryRequestModel);

            if (updateCategoryById) {
                System.out.println("updateCategoryById = " + updateCategoryById);

                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of(
                        "message", "Category updated with this id:- " + id,
                        "status", HttpStatus.OK
                ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                        "message", "something went wrong" + id,
                        "status", HttpStatus.NOT_FOUND
                ));
            }
        }catch (CategoryNotFoundException ex){

            System.out.println("CategoryNotFoundException while updating the category data: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message", ex.getMessage(),
                            "status", HttpStatus.NOT_FOUND
                    ));
        }catch (DataIntegrityViolationException ex) {
            System.out.println("DataIntegrityViolationException while updating the category data: " + ex.getMessage());

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message", ex.getMessage(),
                            "status", HttpStatus.CONFLICT
                    ));
        } catch (RuntimeException e) {
            System.out.println("Exception while updating the category data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", e.getMessage(),
                            "status", HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }

    }
}
