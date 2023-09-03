package com.helmes.app.domain.industry.dto;

import lombok.Builder;
import lombok.Value;

@Builder @Value
public class CategoryStackDto {
    Long id;
    String name;
}
