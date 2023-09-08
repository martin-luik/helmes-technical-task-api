package com.helmes.app.domain.industry.service;

import com.helmes.app.domain.industry.model.Category;
import com.helmes.app.domain.industry.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    private static final String CATEGORY_CACHE = "categoryCache";

    private final CategoryRepository categoryRepository;
    private final CacheManager cacheManager;

    private void validate(Category category) {
        Objects.requireNonNull(category, "Category must be not null");
        Objects.requireNonNull(category.getName(), "Category name cannot be empty");
        Objects.requireNonNull(category.getStatus(), "Category status cannot be empty");

        if (!category.getStatus()) {
            throw new IllegalStateException("Category status cannot be false");
        }
    }

    private void validateWithCategoryId(Category category) {
        validate(category);
        Objects.requireNonNull(category.getId(), "Category id cannot be empty");
    }

    @PostConstruct
    public void initializeCache() {
        Objects.requireNonNull(cacheManager.getCache("categoryCache")).clear();

    }

    @Transactional(readOnly = true)
    public Optional<Category> getById(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CATEGORY_CACHE)
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Transactional
    @CacheEvict(value = CATEGORY_CACHE, allEntries = true)
    public void add(Category category) {
        log.info("Adding category: {}", category);
        validate(category);
        categoryRepository.save(category);
    }

    @Transactional
    @CacheEvict(value = CATEGORY_CACHE, allEntries = true)
    public void edit(Category category) {
        log.info("Editing category: {}", category);
        validateWithCategoryId(category);

        Long categoryRelationId = category.getRelationId();

        if (categoryRelationId != null) {
            getById(categoryRelationId)
                    .orElseThrow(() -> new IllegalStateException(format("Category with relation [id: %d] not found", categoryRelationId)));
        }

        if (isChildCategory(category)) {
            throw new IllegalStateException("Parent component cannot be a child of its own child");
        }

        updateRootCategory(category);
    }

    private boolean isChildCategory(Category category) {
        Queue<Category> queue = new LinkedList<>();
        queue.add(category);

        while (!queue.isEmpty()) {
            Category currentCategory = queue.poll();

            List<Category> childCategories = categoryRepository.findAllByRelationId(currentCategory.getId());
            for (Category childCategory : childCategories) {
                if (childCategory.getId().equals(currentCategory.getRelationId())) {
                    return true;
                }
                queue.add(childCategory);
            }
        }
        return false;
    }

    private void updateRootCategory(Category category) {
        List<Category> categories = new ArrayList<>();
        Queue<Category> queue = new LinkedList<>();
        queue.add(category);

        while (!queue.isEmpty()) {
            Category currentCategory = queue.poll();
            categories.add(currentCategory);

            log.info("Processing category: {}", currentCategory);

            List<Category> childCategories = categoryRepository.findAllByRelationId(currentCategory.getId());
            childCategories.forEach(childCategory -> {
                childCategory.setRelationId(currentCategory.getId());
                queue.add(childCategory);
            });
        }

        categoryRepository.saveAll(categories);
    }

    @Transactional
    @CacheEvict(value = CATEGORY_CACHE, allEntries = true)
    public void delete(Category category) {
        log.info("Deleting category: {}", category);
        validateWithCategoryId(category);
        List<Category> categories = new ArrayList<>();

        Queue<Category> categoryQueue = new LinkedList<>();
        categoryQueue.add(category);

        while (!categoryQueue.isEmpty()) {
            Category currentCategory = categoryQueue.poll();
            categories.add(currentCategory);

            log.info("Processing category: {}", currentCategory);

            List<Category> childCategories = categoryRepository.findAllByRelationId(currentCategory.getId());
            categoryQueue.addAll(childCategories);
        }

        categoryRepository.deleteAll(categories);
    }
}
