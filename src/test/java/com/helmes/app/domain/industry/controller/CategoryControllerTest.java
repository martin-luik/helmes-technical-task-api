package com.helmes.app.domain.industry.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helmes.app.common.ControllerExceptionHandler;
import com.helmes.app.domain.industry.dto.AddCategoryDto;
import com.helmes.app.domain.industry.dto.CategoryTreeDto;
import com.helmes.app.domain.industry.dto.EditCategoryDto;
import com.helmes.app.domain.industry.model.Category;
import com.helmes.app.domain.industry.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @Test
    void get() throws Exception {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setRelationId(null);
        category1.setName("Manufacturing");
        category1.setStatus(true);

        Category category2 = new Category();
        category2.setId(2L);
        category2.setRelationId(1L);
        category2.setName("Construction materials");
        category2.setStatus(true);

        Category category3 = new Category();
        category3.setId(4L);
        category3.setRelationId(1L);
        category3.setName("Food and Beverage");
        category3.setStatus(true);

        Category category4 = new Category();
        category4.setId(5L);
        category4.setRelationId(4L);
        category4.setName("Bakery & confectionery products");
        category4.setStatus(true);

        Category category5 = new Category();
        category5.setId(7L);
        category5.setRelationId(4L);
        category5.setName("Beverages");
        category5.setStatus(true);

        Category category6 = new Category();
        category6.setId(64L);
        category6.setRelationId(null);
        category6.setName("Other");
        category6.setStatus(true);

        Category category7 = new Category();
        category7.setId(65L);
        category7.setRelationId(64L);
        category7.setName("Creative industries");
        category7.setStatus(true);

        Category category8 = new Category();
        category8.setId(66L);
        category8.setRelationId(64L);
        category8.setName("Energy technology");
        category8.setStatus(true);

        Category category9 = new Category();
        category9.setId(67L);
        category9.setRelationId(64L);
        category9.setName("Environment");
        category9.setStatus(true);

        List<Category> categories = List.of(
                category1,
                category2,
                category3,
                category4,
                category5,
                category6,
                category7,
                category8,
                category9
        );

        String expectedCategoryTree = "[{\"id\":1,\"relationId\":null,\"name\":\"Manufacturing\",\"status\":true,\"childCategories\":[{\"id\":2,\"relationId\":1,\"name\":\"Construction materials\",\"status\":true,\"childCategories\":[]},{\"id\":4,\"relationId\":1,\"name\":\"Food and Beverage\",\"status\":true,\"childCategories\":[{\"id\":5,\"relationId\":4,\"name\":\"Bakery & confectionery products\",\"status\":true,\"childCategories\":[]},{\"id\":7,\"relationId\":4,\"name\":\"Beverages\",\"status\":true,\"childCategories\":[]}]}]},{\"id\":64,\"relationId\":null,\"name\":\"Other\",\"status\":true,\"childCategories\":[{\"id\":65,\"relationId\":64,\"name\":\"Creative industries\",\"status\":true,\"childCategories\":[]},{\"id\":66,\"relationId\":64,\"name\":\"Energy technology\",\"status\":true,\"childCategories\":[]},{\"id\":67,\"relationId\":64,\"name\":\"Environment\",\"status\":true,\"childCategories\":[]}]}]";

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/categories")
                .accept(MediaType.APPLICATION_JSON);

        doReturn(categories).when(categoryService).getAll();

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(expectedCategoryTree, result.getResponse().getContentAsString());
    }

    @Test
    void add() throws Exception {
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("test1");
        existingCategory.setStatus(true);

        AddCategoryDto addCategoryDto = new AddCategoryDto();
        addCategoryDto.setRelationId(1L);
        addCategoryDto.setName("Test2");
        addCategoryDto.setStatus(true);

        Category newCategory = new Category();
        newCategory.setRelationId(addCategoryDto.getRelationId());
        newCategory.setName(addCategoryDto.getName());
        newCategory.setStatus(addCategoryDto.getStatus());

        Category addedCategory = new Category();
        addedCategory.setId(2L);
        addedCategory.setRelationId(newCategory.getRelationId());
        addedCategory.setName(newCategory.getName());
        addedCategory.setStatus(newCategory.getStatus());

        List<CategoryTreeDto> expectedCategoryTreeDto = List.of(new CategoryTreeDto(
                existingCategory.getId(),
                existingCategory.getRelationId(),
                existingCategory.getName(),
                existingCategory.getStatus(),
                List.of(new CategoryTreeDto(
                        addedCategory.getId(),
                        addedCategory.getRelationId(),
                        addedCategory.getName(),
                        addedCategory.getStatus(),
                        Collections.emptyList()
                ))
        ));


        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/categories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addCategoryDto));

        doReturn(List.of(
                existingCategory,
                addedCategory
        )).when(categoryService).getAll();

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        verify(categoryService).add(newCategory);
        assertEquals(objectMapper.writeValueAsString(expectedCategoryTreeDto), result.getResponse().getContentAsString());
    }

    @Test
    void add_Returns400_WhenStatusIsNull() throws Exception {
        AddCategoryDto addCategoryDto = new AddCategoryDto();
        addCategoryDto.setRelationId(1L);
        addCategoryDto.setName("Test");

        ControllerExceptionHandler.ErrorResponse errorResponse = ControllerExceptionHandler.ErrorResponse.builder()
                .status(400)
                .message("Validation error")
                .errors(List.of("error.validation.category.status"))
                .build();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/categories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addCategoryDto));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(objectMapper.writeValueAsString(errorResponse), result.getResponse().getContentAsString());
    }

    @Test
    void add_Returns400_WhenStatusIsFalse() throws Exception {
        AddCategoryDto addCategoryDto = new AddCategoryDto();
        addCategoryDto.setRelationId(1L);
        addCategoryDto.setName("Test");
        addCategoryDto.setStatus(false);

        ControllerExceptionHandler.ErrorResponse errorResponse = ControllerExceptionHandler.ErrorResponse.builder()
                .status(400)
                .message("Validation error")
                .errors(List.of("error.validation.category.status"))
                .build();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/categories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addCategoryDto));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(objectMapper.writeValueAsString(errorResponse), result.getResponse().getContentAsString());
    }

    @Test
    void add_Returns400_WhenNameIsEmpty() throws Exception {
        AddCategoryDto addCategoryDto = new AddCategoryDto();
        addCategoryDto.setRelationId(1L);
        addCategoryDto.setName("");
        addCategoryDto.setStatus(true);

        ControllerExceptionHandler.ErrorResponse errorResponse = ControllerExceptionHandler.ErrorResponse.builder()
                .status(400)
                .message("Validation error")
                .errors(List.of("error.validation.category.name"))
                .build();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/categories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addCategoryDto));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(objectMapper.writeValueAsString(errorResponse), result.getResponse().getContentAsString());
    }

    @Test
    void add_Returns400_WhenNameIsNull() throws Exception {
        AddCategoryDto addCategoryDto = new AddCategoryDto();
        addCategoryDto.setRelationId(1L);
        addCategoryDto.setStatus(true);

        ControllerExceptionHandler.ErrorResponse errorResponse = ControllerExceptionHandler.ErrorResponse.builder()
                .status(400)
                .message("Validation error")
                .errors(List.of("error.validation.category.name"))
                .build();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/categories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addCategoryDto));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(objectMapper.writeValueAsString(errorResponse), result.getResponse().getContentAsString());
    }

    @Test
    void edit() throws Exception {
        Category existingCategory1 = new Category();
        existingCategory1.setId(1L);
        existingCategory1.setName("test1");
        existingCategory1.setStatus(true);

        Category existingCategory2 = new Category();
        existingCategory2.setId(2L);
        existingCategory2.setName("test2");
        existingCategory2.setStatus(true);

        EditCategoryDto editCategoryDto = new EditCategoryDto();
        editCategoryDto.setId(existingCategory2.getId());
        editCategoryDto.setRelationId(existingCategory1.getId());
        editCategoryDto.setName("New name");

        Category editedCategory = new Category();
        editedCategory.setId(editCategoryDto.getId());
        editedCategory.setRelationId(editCategoryDto.getRelationId());
        editedCategory.setName(editCategoryDto.getName());
        editedCategory.setStatus(existingCategory2.getStatus());

        List<CategoryTreeDto> expectedCategoryTreeDto = List.of(new CategoryTreeDto(
                existingCategory1.getId(),
                null,
                existingCategory1.getName(),
                existingCategory1.getStatus(),
                List.of(new CategoryTreeDto(
                        existingCategory2.getId(),
                        editedCategory.getRelationId(),
                        editedCategory.getName(),
                        existingCategory2.getStatus(),
                        Collections.emptyList()
                ))
        ));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/categories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editCategoryDto));

        doReturn(Optional.of(existingCategory2)).when(categoryService).getById(editCategoryDto.getId());

        doReturn(List.of(
                existingCategory1,
                editedCategory
        )).when(categoryService).getAll();

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        verify(categoryService).edit(editedCategory);
        assertEquals(objectMapper.writeValueAsString(expectedCategoryTreeDto), result.getResponse().getContentAsString());
    }

    @Test
    void edit_Returns400_WhenCategoryNotFound() throws Exception {
        EditCategoryDto editCategoryDto = new EditCategoryDto();
        editCategoryDto.setId(1L);
        editCategoryDto.setRelationId(1L);
        editCategoryDto.setName("Test");

        ControllerExceptionHandler.ErrorResponse errorResponse = ControllerExceptionHandler.ErrorResponse.builder()
                .status(400)
                .message("Category not exists")
                .errors(List.of("Category not exists"))
                .build();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/categories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editCategoryDto));

        doReturn(Optional.empty()).when(categoryService).getById(editCategoryDto.getId());

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(objectMapper.writeValueAsString(errorResponse), result.getResponse().getContentAsString());
    }

    @Test
    void edit_Return400_WhenNameIsEmpty() throws Exception {
        EditCategoryDto editCategoryDto = new EditCategoryDto();
        editCategoryDto.setId(1L);
        editCategoryDto.setRelationId(1L);
        editCategoryDto.setName("");

        ControllerExceptionHandler.ErrorResponse errorResponse = ControllerExceptionHandler.ErrorResponse.builder()
                .status(400)
                .message("Validation error")
                .errors(List.of("error.validation.category.name"))
                .build();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/categories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editCategoryDto));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(objectMapper.writeValueAsString(errorResponse), result.getResponse().getContentAsString());
    }

    @Test
    void delete() throws Exception {
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("test1");
        existingCategory.setStatus(true);

        List<CategoryTreeDto> expectedCategoryTreeDto = Collections.emptyList();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/categories/" + existingCategory.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        doReturn(Optional.of(existingCategory)).when(categoryService).getById(existingCategory.getId());

        doReturn(Collections.emptyList()).when(categoryService).getAll();

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        verify(categoryService).delete(existingCategory);
        assertEquals(objectMapper.writeValueAsString(expectedCategoryTreeDto), result.getResponse().getContentAsString());
    }

    @Test
    void delete_Return400_WhenIdIsEmpty() throws Exception {
        ControllerExceptionHandler.ErrorResponse errorResponse = ControllerExceptionHandler.ErrorResponse.builder()
                .status(400)
                .message("Category not exists")
                .errors(List.of("Category not exists"))
                .build();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/categories/" + 1L)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(objectMapper.writeValueAsString(errorResponse), result.getResponse().getContentAsString());
    }
}