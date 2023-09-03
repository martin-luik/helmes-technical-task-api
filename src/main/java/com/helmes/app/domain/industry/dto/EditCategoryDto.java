package com.helmes.app.domain.industry.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class EditCategoryDto {
    protected Long id;
    private Long relationId;
    @NotEmpty(message = "error.validation.category.name")
    private String name;
}