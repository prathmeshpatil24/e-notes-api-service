package com.enotes.service;

import com.enotes.dto.ActiveCategoryModel;
import com.enotes.dto.CategoryRequestModel;
import com.enotes.dto.CategoryResponseModel;
import com.enotes.entity.Category;

import java.util.List;

public interface CategoryService {

    public CategoryResponseModel saveCategory(CategoryRequestModel categoryRequestModel);

    public List<CategoryResponseModel> getAllCategory();

    public List<ActiveCategoryModel> getOnlyActiveCategory();

    public CategoryResponseModel getCategoryById(Integer categoryId);

    public Boolean deleteCategoryById(Integer categoryId);

    public Boolean updateCategoryById(Integer categoryId, CategoryRequestModel categoryRequestModel);
}
