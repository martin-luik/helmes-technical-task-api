package com.helmes.app.domain.industry.controller;

import com.helmes.app.domain.industry.dto.AddCategoryDto;
import com.helmes.app.domain.industry.dto.CategoryTreeDto;
import com.helmes.app.domain.industry.dto.EditCategoryDto;
import com.helmes.app.domain.industry.exception.BusinessError;
import com.helmes.app.domain.industry.exception.BusinessLogicException;
import com.helmes.app.domain.industry.model.Category;
import com.helmes.app.domain.industry.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryTreeDto>> getAllCategories() {
        List<Category> categories = categoryService.getAll();
        return new ResponseEntity<>(buildCategoryTreeDto(categories), HttpStatus.OK);
    }

    @PostMapping("/categories")
    public ResponseEntity<List<CategoryTreeDto>> addCategory(@Valid @RequestBody AddCategoryDto addCategoryDto) {
        Category category = new Category();
        category.setRelationId(addCategoryDto.getRelationId());
        category.setName(addCategoryDto.getName());
        category.setStatus(addCategoryDto.getStatus());

        categoryService.add(category);

        List<Category> categories = categoryService.getAll();

        return new ResponseEntity<>(buildCategoryTreeDto(categories), HttpStatus.OK);
    }

    @PutMapping("/categories")
    public ResponseEntity<List<CategoryTreeDto>> editCategory(@Valid @RequestBody EditCategoryDto editCategoryDto) {
        Category category = categoryService.getById(editCategoryDto.getId())
                .orElseThrow(() -> new BusinessLogicException(BusinessError.CATEGORY_NOT_FOUND));

        category.setRelationId(editCategoryDto.getRelationId());
        category.setName(editCategoryDto.getName());

        categoryService.edit(category);

        List<Category> categories = categoryService.getAll();

        return new ResponseEntity<>(buildCategoryTreeDto(categories), HttpStatus.OK);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<List<CategoryTreeDto>> deleteCategory(@PathVariable Long id) {
        Category category = categoryService.getById(id)
                .orElseThrow(() -> new BusinessLogicException(BusinessError.CATEGORY_NOT_FOUND));

        categoryService.delete(category);

        List<Category> categories = categoryService.getAll();

        return new ResponseEntity<>(buildCategoryTreeDto(categories), HttpStatus.OK);
    }

    private List<CategoryTreeDto> buildCategoryTreeDto(List<Category> categoryList) {
        List<CategoryTreeDto> topLevelCategories = new ArrayList<>();
        for (Category category : categoryList) {
            if (category.getRelationId() == null) {
                topLevelCategories.add(composeTree(category, categoryList));
            }
        }

        topLevelCategories.sort(Comparator.comparing(CategoryTreeDto::getName));
        return topLevelCategories;
    }

    private CategoryTreeDto composeTree(Category parentCategory, List<Category> categoryList) {
        CategoryTreeDto categoryTreeDto = CategoryTreeDto.builder()
                .id(parentCategory.getId())
                .relationId(parentCategory.getRelationId())
                .name(parentCategory.getName())
                .status(parentCategory.getStatus())
                .build();

        List<CategoryTreeDto> childCategoryTreeDto = new ArrayList<>();
        for (Category category : categoryList) {
            if (category.getRelationId() != null && category.getRelationId().equals(parentCategory.getId())) {
                childCategoryTreeDto.add(composeTree(category, categoryList));
            }
        }
        childCategoryTreeDto.sort(Comparator.comparing(CategoryTreeDto::getName));
        return categoryTreeDto.withChildCategories(childCategoryTreeDto);
    }
}
