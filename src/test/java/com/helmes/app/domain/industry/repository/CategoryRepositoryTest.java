package com.helmes.app.domain.industry.repository;

import com.helmes.app.domain.industry.model.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void findById() {
        Category expectedCategory = new Category();
        expectedCategory.setName("Test");
        expectedCategory.setStatus(true);

        Category result = categoryRepository.save(expectedCategory);

        Category category = categoryRepository.findById(result.getId()).orElse(null);

        assertEquals(result, category);
    }

    @Test
    void findAll() {
        Category category1 = new Category();
        category1.setName("Test1");
        category1.setStatus(true);

        Category expectedCategory1 = categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setRelationId(expectedCategory1.getId());
        category2.setName("Test2");
        category2.setStatus(true);

        Category expectedCategory2 = categoryRepository.save(category2);

        List<Category> categories = categoryRepository.findAll();

        assertNotEquals(0, categories.size());
        assertTrue(categories.containsAll(List.of(expectedCategory1, expectedCategory2)));
    }

    @Test
    void save() {
        Category category = new Category();
        category.setName("Test3");
        category.setStatus(true);

        Category savedCategory = categoryRepository.save(category);

        Category result = categoryRepository.findById(savedCategory.getId()).orElse(null);

        assertEquals(savedCategory, result);
    }

    @Test
    void findAllByRelationId() {
        Category category1 = new Category();
        category1.setName("Test1");
        category1.setStatus(true);

        Category expectedCategory1 = categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setRelationId(expectedCategory1.getId());
        category2.setName("Test2");
        category2.setStatus(true);

        Category expectedCategory2 = categoryRepository.save(category2);

        List<Category> categories = categoryRepository.findAllByRelationId(expectedCategory1.getId());

        assertNotEquals(0, categories.size());
        assertTrue(categories.contains(expectedCategory2));
    }
}