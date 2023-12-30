package com.helmes.app.industry.model.mapper;

import com.helmes.app.industry.model.dto.AddCategoryDto;
import com.helmes.app.industry.model.entity.Category;

public class AddCategoryDtoToCategory {
    
    public static Category map(AddCategoryDto addCategoryDto) {
        Category category = new Category();
        category.setRelationId(addCategoryDto.getRelationId());
        category.setName(addCategoryDto.getName());
        category.setStatus(addCategoryDto.getStatus());
        return category;
    }
}
