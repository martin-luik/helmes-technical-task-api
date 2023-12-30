package com.helmes.app.industry.model.mapper;

import com.helmes.app.industry.model.dto.EditCategoryDto;
import com.helmes.app.industry.model.entity.Category;

public class EditCategoryDtoToCategory {

    public static Category map(EditCategoryDto editCategoryDto, Category category) {
        category.setRelationId(editCategoryDto.getRelationId());
        category.setName(editCategoryDto.getName());
        return category;
    }
}
