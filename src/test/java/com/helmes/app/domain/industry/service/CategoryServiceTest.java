package com.helmes.app.domain.industry.service;

import com.helmes.app.domain.industry.model.Category;
import com.helmes.app.domain.industry.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getById() {
        Category expectedCategory = new Category();
        expectedCategory.setId(1L);
        expectedCategory.setName("Test1");
        expectedCategory.setStatus(true);

        doReturn(Optional.of(expectedCategory)).when(categoryRepository).findById(1L);

        Optional<Category> category = categoryService.getById(1L);

        assertEquals(Optional.of(expectedCategory), category);
    }

    @Test
    void getAll() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Test1");
        category1.setStatus(true);
        Category category2 = new Category();
        category2.setId(2L);
        category2.setRelationId(1L);
        category2.setName("Test2");
        category2.setStatus(true);
        Category category3 = new Category();
        category3.setId(3L);
        category3.setName("Test3");
        category3.setStatus(true);

        List<Category> expectedCategories = List.of(
                category1,
                category2,
                category3
        );


        doReturn(expectedCategories).when(categoryRepository).findAll();

        List<Category> categories = categoryService.getAll();

        assertEquals(expectedCategories, categories);
    }

    @Test
    void add() {
        Category category = new Category();
        category.setName("Test");
        category.setRelationId(1L);
        category.setStatus(true);

        categoryService.add(category);

        verify(categoryRepository).save(category);
    }

    @Test
    void edit() {
        Category category = new Category();
        category.setName("Test");
        category.setRelationId(1L);
        category.setStatus(true);

        doReturn(Optional.of(category)).when(categoryRepository).findById(1L);

        categoryService.edit(category);

        verify(categoryRepository).save(category);
    }

    @Test
    void delete() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Test");
        category.setRelationId(2L);
        category.setStatus(true);

        categoryService.delete(category);

        verify(categoryRepository).delete(category);
    }
}