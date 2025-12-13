package com.enotes.category.controller;

import com.enotes.dto.ActiveCategoryModel;
import com.enotes.dto.CategoryRequestModel;
import com.enotes.dto.CategoryResponseModel;
import com.enotes.category.service.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<?> saveCategory(@RequestBody CategoryRequestModel categoryRequestModel) {
            CategoryResponseModel saveCategory = categoryService.saveCategory(categoryRequestModel);

            return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of(
                                "message", "new category saved with id:- " +
                                        saveCategory.getId() + " name:- " +
                                        saveCategory.getName(),
                                "status", HttpStatus.CREATED
                        ));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllCategory() {

        List<CategoryResponseModel> allCategory = categoryService.getAllCategory();
                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of(
                                "message", "All categories retrieved successfully",
                                "status", HttpStatus.OK,
                                "data", allCategory
                        ));

    }

    @GetMapping("/active")
    public ResponseEntity<?> getOnlyActiveCategory() {

            List<ActiveCategoryModel> onlyActiveCategory = categoryService.getOnlyActiveCategory();

                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of(
                                "message", "Active categories retrieved successfully",
                                "status", HttpStatus.OK,
                                "data", onlyActiveCategory
                        ));
    }

    @GetMapping("/inActive")
    public ResponseEntity<?> getOnlyInActiveCategory() {

        List<ActiveCategoryModel> inActiveCategory = categoryService.getOnlyInActiveCategory();

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", "InActive categories retrieved successfully",
                        "status", HttpStatus.OK,
                        "data", inActiveCategory
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Integer id) {

            CategoryResponseModel category = categoryService.getCategoryById(id);

                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of(
                        "message", "Category found with this id:- " + id,
                        "status", HttpStatus.OK,
                        "data", category
                ));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> disableCategoryById(@PathVariable Integer id) {

            categoryService.disableCategoryById(id);

                System.out.println("deleteCategoryById:- " + id);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of(
                        "message", "Category disabled with this id:- " + id,
                        "status", HttpStatus.OK
                ));

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCategoryById(@PathVariable Integer id, @RequestBody CategoryRequestModel categoryRequestModel) {

            CategoryResponseModel responseModel = categoryService.updateCategoryById(id, categoryRequestModel);

                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of(
                        "message", "Category updated with this id:- " + responseModel.getId(),
                        "status", HttpStatus.OK
                ));
    }
}
