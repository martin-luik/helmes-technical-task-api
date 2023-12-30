package com.helmes.app.industry.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.helmes.app.industry.exception.BusinessError;
import com.helmes.app.industry.exception.BusinessLogicException;
import com.helmes.app.industry.model.dto.AddCategoryDto;
import com.helmes.app.industry.model.dto.CategoryTreeDto;
import com.helmes.app.industry.model.dto.EditCategoryDto;
import com.helmes.app.industry.model.entity.Category;
import com.helmes.app.industry.model.mapper.AddCategoryDtoToCategory;
import com.helmes.app.industry.model.mapper.CategoriesToCategoryTreeDto;
import com.helmes.app.industry.model.mapper.EditCategoryDtoToCategory;
import com.helmes.app.industry.service.CategoryService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryTreeDto>> getAllCategories() {
        List<Category> categories = categoryService.getAll();
        return new ResponseEntity<>(CategoriesToCategoryTreeDto.map(categories), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<List<CategoryTreeDto>> addCategory(@Valid @RequestBody AddCategoryDto addCategoryDto) {
        Category category = AddCategoryDtoToCategory.map(addCategoryDto);
        categoryService.add(category);
        List<Category> categories = categoryService.getAll();
        return new ResponseEntity<>(CategoriesToCategoryTreeDto.map(categories), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<List<CategoryTreeDto>> editCategory(@Valid @RequestBody EditCategoryDto editCategoryDto) {
        Category category = categoryService.getById(editCategoryDto.getId())
                .orElseThrow(() -> new BusinessLogicException(BusinessError.CATEGORY_NOT_FOUND));

        categoryService.edit(EditCategoryDtoToCategory.map(editCategoryDto, category));

        List<Category> categories = categoryService.getAll();

        return new ResponseEntity<>(CategoriesToCategoryTreeDto.map(categories), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<List<CategoryTreeDto>> deleteCategory(@PathVariable Long id) {
        Category category = categoryService.getById(id)
                .orElseThrow(() -> new BusinessLogicException(BusinessError.CATEGORY_NOT_FOUND));

        categoryService.delete(category);

        List<Category> categories = categoryService.getAll();

        return new ResponseEntity<>(CategoriesToCategoryTreeDto.map(categories), HttpStatus.OK);
    }
}
