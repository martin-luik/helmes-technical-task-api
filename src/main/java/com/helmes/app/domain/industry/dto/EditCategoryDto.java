package com.helmes.app.domain.industry.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EditCategoryDto {
    protected Long id;
    private Long relationId;
    @NotEmpty(message = "error.validation.category.name")
    private String name;
}