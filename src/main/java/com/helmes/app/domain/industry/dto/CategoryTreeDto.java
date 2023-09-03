package com.helmes.app.domain.industry.dto;

import lombok.*;

import java.util.List;

@Builder @Value @With
public class CategoryTreeDto {
    Long id;
    Long relationId;
    String name;
    boolean status;
    List<CategoryTreeDto> childCategories;
}