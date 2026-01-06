package com.enotes.category.service;

import com.enotes.dto.ActiveCategoryModel;
import com.enotes.category.dto.CategoryRequestModel;
import com.enotes.category.dto.CategoryResponseModel;

import java.util.List;

public interface CategoryService {

    public CategoryResponseModel saveCategory(CategoryRequestModel categoryRequestModel);

    void saveBulkCategory(List<CategoryRequestModel>categoryRequestModels);

    public List<CategoryResponseModel> getAllCategory();

    public List<ActiveCategoryModel> getOnlyActiveCategory();

    public List<ActiveCategoryModel> getOnlyInActiveCategory();

    public CategoryResponseModel getCategoryById(Integer categoryId);

    public void disableCategoryById(Integer categoryId);

    public CategoryResponseModel updateCategoryById(Integer categoryId, CategoryRequestModel categoryRequestModel);
}
