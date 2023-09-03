package com.helmes.app.domain.industry.repository;

import com.helmes.app.domain.industry.model.Category;
import jakarta.annotation.Nonnull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    @Nonnull
    List<Category> findAll();

    List<Category> findAllByRelationId(Long relationId);
}
