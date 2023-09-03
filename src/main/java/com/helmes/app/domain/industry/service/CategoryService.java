package com.helmes.app.domain.industry.service;

import com.helmes.app.common.exception.BusinessError;
import com.helmes.app.common.exception.BusinessLogicException;
import com.helmes.app.domain.industry.model.Category;
import com.helmes.app.domain.industry.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Optional<Category> getById(Long id) {
        return categoryRepository.findById(id);
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Transactional
    public void add(Category category) {
        categoryRepository.save(category);
    }

    @Transactional
    public void edit(Category category) {
        Long categoryRelationId = category.getRelationId();

        if (categoryRelationId != null) {
            getById(categoryRelationId)
                    .orElseThrow(() -> new IllegalStateException(format("Category with relation [id: %d] not found", categoryRelationId )));
        }

        if (isChildCategory(category)) {
            throw new IllegalStateException("Parent component cannot be a child of its own child");
        }

        updateRootCategory(category);
    }

    private boolean isChildCategory(Category category) {
        List<Category> childCategories = categoryRepository.findAllByRelationId(category.getId());

        for (Category childCategory : childCategories) {
            if (childCategory.getId().equals(category.getRelationId())) {
                return true;
            }
            isChildCategory(childCategory);
        }
        return false;
    }

    private void updateRootCategory(Category category) {
        categoryRepository.save(category);

        List<Category> categories = categoryRepository.findAllByRelationId(category.getId());
        for (Category childCategory : categories) {
            childCategory.setRelationId(category.getId());
            updateRootCategory(childCategory);
        }
    }

    @Transactional
    public void delete(Category category) {
        deleteRootCategory(category);
    }

    private void deleteRootCategory(Category category) {
        categoryRepository.delete(category);

        List<Category> categories = categoryRepository.findAllByRelationId(category.getId());
        for (Category childCategory : categories) {
            deleteRootCategory(childCategory);
        }
    }
}
