package com.enotes.category.controller;

import com.enotes.category.service.CategoryServiceImpl;
import com.enotes.dto.ActiveCategoryModel;
import com.enotes.category.dto.CategoryRequestModel;
import com.enotes.category.dto.CategoryResponseModel;
import com.enotes.category.service.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryServiceImpl categoryService;

    @PostMapping("/create")
    public ResponseEntity<?> saveCategory(@RequestBody CategoryRequestModel categoryRequestModel) {
        CategoryResponseModel saveCategory = categoryService.saveCategory(categoryRequestModel);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "new category saved with id:- " +
                                saveCategory.getId() + " name:- " +
                                saveCategory.getName(),
                        "status", HttpStatus.CREATED,
                        "timestamp", LocalDateTime.now()
                ));
    }

    @PostMapping("/create/bulk")
    public ResponseEntity<?> saveBulkCategory(@RequestBody List<CategoryRequestModel> categoryRequestModels) {

        categoryService.saveBulkCategory(categoryRequestModels);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Bulk categories saved successfully",
                        "status", HttpStatus.CREATED,
                        "timestamp", LocalDateTime.now()
                ));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllCategory() {

        List<CategoryResponseModel> allCategory = categoryService.getAllCategory();
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", "All categories retrieved successfully",
                        "status", HttpStatus.OK,
                        "data", allCategory,
                        "timestamp", LocalDateTime.now()
                ));

    }

    @GetMapping("/active")
    public ResponseEntity<?> getOnlyActiveCategory() {

        List<ActiveCategoryModel> onlyActiveCategory = categoryService.getOnlyActiveCategory();

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", "Active categories retrieved successfully",
                        "status", HttpStatus.OK,
                        "data", onlyActiveCategory,
                        "timestamp", LocalDateTime.now()
                ));
    }

    @GetMapping("/inActive")
    public ResponseEntity<?> getOnlyInActiveCategory() {

        List<ActiveCategoryModel> inActiveCategory = categoryService.getOnlyInActiveCategory();

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", "InActive categories retrieved successfully",
                        "status", HttpStatus.OK,
                        "data", inActiveCategory,
                        "timestamp", LocalDateTime.now()
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Integer id) {

        CategoryResponseModel category = categoryService.getCategoryById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", "Category found with this id:- " + id,
                        "status", HttpStatus.OK,
                        "data", category,
                        "timestamp", LocalDateTime.now()
                ));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> disableCategoryById(@PathVariable Integer id) {

        categoryService.disableCategoryById(id);

        System.out.println("deleteCategoryById:- " + id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", "Category disabled with this id:- " + id,
                        "status", HttpStatus.OK,
                        "timestamp", LocalDateTime.now()
                ));

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCategoryById(@PathVariable Integer id, @RequestBody CategoryRequestModel categoryRequestModel) {

        CategoryResponseModel responseModel = categoryService.updateCategoryById(id, categoryRequestModel);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", "Category updated with this id:- " + responseModel.getId(),
                        "status", HttpStatus.OK,
                        "timestamp", LocalDateTime.now()
                ));
    }
}
