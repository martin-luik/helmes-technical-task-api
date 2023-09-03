package com.helmes.app.domain.industry.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class DeleteCategoryDto {
    @NotNull(message = "error.validation.category.id")
    protected Long id;
}