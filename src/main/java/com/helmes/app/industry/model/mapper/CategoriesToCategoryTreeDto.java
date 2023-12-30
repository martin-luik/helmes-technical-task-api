package com.helmes.app.industry.model.mapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.helmes.app.industry.model.dto.CategoryTreeDto;
import com.helmes.app.industry.model.entity.Category;

public class CategoriesToCategoryTreeDto {

    public static List<CategoryTreeDto> map(List<Category> categoryList) {
        return buildCategoryTreeDto(categoryList);
    }

    private static List<CategoryTreeDto> buildCategoryTreeDto(List<Category> categoryList) {
        return categoryList.stream()
                .filter(category -> category.getRelationId() == null)
                .map(category -> composeCategoryTreeDto(category, categoryList))
                .sorted(Comparator.comparing(CategoryTreeDto::getName))
                .toList();
    }

    private static CategoryTreeDto composeCategoryTreeDto(Category parentCategory, List<Category> categoryList) {
        CategoryTreeDto categoryTreeDto = new CategoryTreeDto();
        categoryTreeDto.setId(parentCategory.getId());
        categoryTreeDto.setRelationId(parentCategory.getRelationId());
        categoryTreeDto.setName(parentCategory.getName());
        categoryTreeDto.setStatus(parentCategory.getStatus());

        List<CategoryTreeDto> childCategoryTreeDto = new ArrayList<>();

        categoryList.stream()
                .filter(category -> category.getRelationId() != null && category.getRelationId().equals(parentCategory.getId()))
                .forEach(category -> childCategoryTreeDto.add(composeCategoryTreeDto(category, categoryList)));

        childCategoryTreeDto.sort(Comparator.comparing(CategoryTreeDto::getName));
        categoryTreeDto.setChildCategories(childCategoryTreeDto);
        return categoryTreeDto;
    }
    
}
