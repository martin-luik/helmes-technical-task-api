package com.helmes.app.industry.repository;

import jakarta.annotation.Nonnull;
import org.springframework.data.repository.CrudRepository;

import com.helmes.app.industry.model.entity.Category;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    @Nonnull
    List<Category> findAll();

    List<Category> findAllByRelationId(Long relationId);
}
