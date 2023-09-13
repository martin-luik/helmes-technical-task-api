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

import java.util.*;

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

        return new ResponseEntity<>(buildCategoryTreeDto(categories), HttpStatus.CREATED);
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
        return categoryList.stream()
                .filter(category -> category.getRelationId() == null)
                .map(category -> composeTree(category, categoryList))
                .sorted(Comparator.comparing(CategoryTreeDto::getName))
                .toList();
    }


    private CategoryTreeDto composeTree(Category rootCategory, List<Category> categoryList) {
        Map<Long, CategoryTreeDto> categoryMap = new HashMap<>();
        Deque<Category> categoryDeque = new LinkedList<>();
        CategoryTreeDto categoryTreeDtoRoot = new CategoryTreeDto();

        categoryDeque.push(rootCategory);
        categoryMap.put(rootCategory.getId(), categoryTreeDtoRoot);

        while (!categoryDeque.isEmpty()) {
            Category currentCategory = categoryDeque.pop();
            CategoryTreeDto currentTreeDto = categoryMap.get(currentCategory.getId());

            currentTreeDto.setId(currentCategory.getId());
            currentTreeDto.setRelationId(currentCategory.getRelationId());
            currentTreeDto.setName(currentCategory.getName());
            currentTreeDto.setStatus(currentCategory.getStatus());

            List<CategoryTreeDto> childCategoryTreeDto = new ArrayList<>();

            categoryList.stream()
                    .filter(category -> Objects.equals(category.getRelationId(), currentCategory.getId()))
                    .forEach(category -> {
                        CategoryTreeDto childTreeDto = new CategoryTreeDto();
                        childCategoryTreeDto.add(childTreeDto);
                        categoryMap.put(category.getId(), childTreeDto);
                        categoryDeque.push(category);
                    });

            sortTree(categoryTreeDtoRoot);
            currentTreeDto.setChildCategories(childCategoryTreeDto);
        }

        return categoryTreeDtoRoot;
    }

    private void sortTree(CategoryTreeDto root) {
        if (root == null) {
            return;
        }

        Deque<CategoryTreeDto> stack = new LinkedList<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            CategoryTreeDto current = stack.pop();
            List<CategoryTreeDto> childCategories = current.getChildCategories();

            if (childCategories != null && !childCategories.isEmpty()) {
                childCategories.sort(Comparator.comparing(dto -> Optional.ofNullable(dto.getName()).orElse("")));

                for (int i = childCategories.size() - 1; i >= 0; i--) {
                    stack.push(childCategories.get(i));
                }
            }
        }
    }

}
