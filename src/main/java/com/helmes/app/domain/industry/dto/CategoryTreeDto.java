package com.helmes.app.domain.industry.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTreeDto {
    Long id;
    Long relationId;
    String name;
    boolean status;
    List<CategoryTreeDto> childCategories;
}