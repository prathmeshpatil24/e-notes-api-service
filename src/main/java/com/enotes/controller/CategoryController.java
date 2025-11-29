package com.enotes.controller;

import com.enotes.dto.ActiveCategoryModel;
import com.enotes.dto.CategoryRequestModel;
import com.enotes.dto.CategoryResponseModel;
import com.enotes.entity.Category;
import com.enotes.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/save-category")
    public ResponseEntity<?> saveCategory(@RequestBody CategoryRequestModel categoryRequestModel) {

        CategoryResponseModel saveCategory = categoryService.saveCategory(categoryRequestModel);
        if (saveCategory!=null) {
            return ResponseEntity.status(HttpStatus.CREATED).body("new category saved with id:- " + saveCategory.getId() + " name:- " + saveCategory.getName());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("not saved");
        }
    }

    @GetMapping("/all-category")
    public ResponseEntity<?> getAllCategory() {

        List<CategoryResponseModel> allCategory = categoryService.getAllCategory();
        if (CollectionUtils.isEmpty(allCategory)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(allCategory);
        }

    }

    @GetMapping("/only-active-category")
    public ResponseEntity<?> getOnlyActiveCategory() {
        List<ActiveCategoryModel> onlyActiveCategory = categoryService.getOnlyActiveCategory();
        if (CollectionUtils.isEmpty(onlyActiveCategory)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(onlyActiveCategory);
        }
    }

    @GetMapping("/category-by-id/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Integer id) {
        CategoryResponseModel categoryById = categoryService.getCategoryById(id);
        if (categoryById == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No category found with this id:- " + id);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(categoryById);
        }
    }

    @DeleteMapping("/delete-category-by-id/{id}")
    public ResponseEntity<?> deleteCategoryById(@PathVariable Integer id) {
        Boolean deleteCategoryById = categoryService.disableCategoryById(id);
        if (deleteCategoryById) {
            return ResponseEntity.status(HttpStatus.OK).body("Category deleted with this id:- " + id);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No category found with this id:- " + id);
        }
    }

    @PutMapping("/update-category-by-id/{id}")
    public ResponseEntity<?> updateCategoryById(@PathVariable Integer id, @RequestBody CategoryRequestModel categoryRequestModel) {
        Boolean updateCategoryById = categoryService.updateCategoryById(id, categoryRequestModel);
        if (updateCategoryById) {
            System.out.println("updateCategoryById = " + updateCategoryById);
            return ResponseEntity.status(HttpStatus.OK).body("Category updated with this id:- " + id);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No category found with this id:- " + id);
        }
    }

}
