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
        expectedCategory.setId(1L);
        expectedCategory.setName("Test");
        expectedCategory.setStatus(true);

        categoryRepository.save(expectedCategory);

        Category category = categoryRepository.findById(expectedCategory.getId()).orElse(null);

        assertEquals(expectedCategory, category);
    }

    @Test
    void findAll() {
        Category expectedCategory1 = new Category();
        expectedCategory1.setId(1L);
        expectedCategory1.setName("Test1");
        expectedCategory1.setStatus(true);

        Category expectedCategory2 = new Category();
        expectedCategory2.setId(2L);
        expectedCategory2.setRelationId(1L);
        expectedCategory2.setName("Test2");
        expectedCategory2.setStatus(true);

        List<Category> expectedCategories = List.of(
                expectedCategory1,
                expectedCategory2
        );

        categoryRepository.saveAll(expectedCategories);

        List<Category> categories = categoryRepository.findAll();

        assertNotEquals(0, categories.size());
        assertTrue(categories.containsAll(expectedCategories));
    }

    @Test
    void save() {
        Category expectedCategory = new Category();
        expectedCategory.setId(3L);
        expectedCategory.setName("Test3");
        expectedCategory.setStatus(true);

        Category category = categoryRepository.save(expectedCategory);

        assertEquals(expectedCategory, category);
    }
}