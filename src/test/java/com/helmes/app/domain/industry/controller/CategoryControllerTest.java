package com.helmes.app.domain.industry.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helmes.app.common.exception.ControllerExceptionHandler;
import com.helmes.app.domain.industry.dto.AddCategoryDto;
import com.helmes.app.domain.industry.dto.CategoryTreeDto;
import com.helmes.app.domain.industry.dto.DeleteCategoryDto;
import com.helmes.app.domain.industry.dto.EditCategoryDto;
import com.helmes.app.domain.industry.model.Category;
import com.helmes.app.domain.industry.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

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
        Category category = new Category();
        category.setId(1L);
        category.setName("Test1");
        category.setStatus(true);

        List<CategoryTreeDto> expectedCategoryTreeDto = List.of(CategoryTreeDto.builder()
                .id(category.getId())
                .relationId(category.getRelationId())
                .name(category.getName())
                .status(category.getStatus())
                .childCategories(Collections.emptyList())
                .build());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/categories")
                .accept(MediaType.APPLICATION_JSON);

        doReturn(List.of(category)).when(categoryService).getAll();

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(objectMapper.writeValueAsString(expectedCategoryTreeDto), result.getResponse().getContentAsString());
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

        List<CategoryTreeDto> expectedCategoryTreeDto = List.of(CategoryTreeDto.builder()
                        .id(existingCategory.getId())
                        .relationId(existingCategory.getRelationId())
                        .name(existingCategory.getName())
                        .status(existingCategory.getStatus())
                        .childCategories(List.of(CategoryTreeDto.builder()
                                .id(addedCategory.getId())
                                .relationId(addedCategory.getRelationId())
                                .name(addedCategory.getName())
                                .status(addedCategory.getStatus())
                                .childCategories(Collections.emptyList())
                                .build()))
                .build());

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

        ControllerExceptionHandler.ValidationErrorResponse validationErrorResponse = ControllerExceptionHandler.ValidationErrorResponse.builder()
                .status(400)
                .message("Validation error")
                .errors(List.of("error.validation.category.status"))
                .build();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/categories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addCategoryDto));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(objectMapper.writeValueAsString(validationErrorResponse), result.getResponse().getContentAsString());
    }

    @Test
    void add_Returns400_WhenStatusIsFalse() throws Exception {
        AddCategoryDto addCategoryDto = new AddCategoryDto();
        addCategoryDto.setRelationId(1L);
        addCategoryDto.setName("Test");
        addCategoryDto.setStatus(false);

        ControllerExceptionHandler.ValidationErrorResponse validationErrorResponse = ControllerExceptionHandler.ValidationErrorResponse.builder()
                .status(400)
                .message("Validation error")
                .errors(List.of("error.validation.category.status"))
                .build();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/categories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addCategoryDto));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(objectMapper.writeValueAsString(validationErrorResponse), result.getResponse().getContentAsString());
    }

    @Test
    void add_Returns400_WhenNameIsEmpty() throws Exception {
        AddCategoryDto addCategoryDto = new AddCategoryDto();
        addCategoryDto.setRelationId(1L);
        addCategoryDto.setName("");
        addCategoryDto.setStatus(true);

        ControllerExceptionHandler.ValidationErrorResponse validationErrorResponse = ControllerExceptionHandler.ValidationErrorResponse.builder()
                .status(400)
                .message("Validation error")
                .errors(List.of("error.validation.category.name"))
                .build();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/categories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addCategoryDto));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(objectMapper.writeValueAsString(validationErrorResponse), result.getResponse().getContentAsString());
    }

    @Test
    void add_Returns400_WhenNameIsNull() throws Exception {
        AddCategoryDto addCategoryDto = new AddCategoryDto();
        addCategoryDto.setRelationId(1L);
        addCategoryDto.setStatus(true);

        ControllerExceptionHandler.ValidationErrorResponse validationErrorResponse = ControllerExceptionHandler.ValidationErrorResponse.builder()
                .status(400)
                .message("Validation error")
                .errors(List.of("error.validation.category.name"))
                .build();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/categories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addCategoryDto));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(objectMapper.writeValueAsString(validationErrorResponse), result.getResponse().getContentAsString());
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

        List<CategoryTreeDto> expectedCategoryTreeDto = List.of(CategoryTreeDto.builder()
                .id(existingCategory1.getId())
                .relationId(null)
                .name(existingCategory1.getName())
                .status(existingCategory1.getStatus())
                .childCategories(List.of(CategoryTreeDto.builder()
                        .id(existingCategory2.getId())
                        .relationId(editedCategory.getRelationId())
                        .name(editedCategory.getName())
                        .status(existingCategory2.getStatus())
                        .childCategories(Collections.emptyList())
                        .build()))
                .build());

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

        ControllerExceptionHandler.ValidationErrorResponse validationErrorResponse = ControllerExceptionHandler.ValidationErrorResponse.builder()
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

        assertEquals(objectMapper.writeValueAsString(validationErrorResponse), result.getResponse().getContentAsString());
    }

    @Test
    void edit_Return400_WhenNameIsEmpty() throws Exception {
        EditCategoryDto editCategoryDto = new EditCategoryDto();
        editCategoryDto.setId(1L);
        editCategoryDto.setRelationId(1L);
        editCategoryDto.setName("");

        ControllerExceptionHandler.ValidationErrorResponse validationErrorResponse = ControllerExceptionHandler.ValidationErrorResponse.builder()
                .status(400)
                .message("Validation error")
                .errors(List.of("error.validation.category.name"))
                .build();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/categories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editCategoryDto));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(objectMapper.writeValueAsString(validationErrorResponse), result.getResponse().getContentAsString());
    }

    @Test
    void delete() throws Exception {
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("test1");
        existingCategory.setStatus(true);

        DeleteCategoryDto deleteCategoryDto = new DeleteCategoryDto();
        deleteCategoryDto.setId(existingCategory.getId());

        List<CategoryTreeDto> expectedCategoryTreeDto = Collections.emptyList();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/categories/" + deleteCategoryDto.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        doReturn(Optional.of(existingCategory)).when(categoryService).getById(deleteCategoryDto.getId());

        doReturn(Collections.emptyList()).when(categoryService).getAll();

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        verify(categoryService).delete(existingCategory);
        assertEquals(objectMapper.writeValueAsString(expectedCategoryTreeDto), result.getResponse().getContentAsString());
    }

    @Test
    void delete_Return400_WhenIdIsEmpty() throws Exception {
        DeleteCategoryDto deleteCategoryDto = new DeleteCategoryDto();
        deleteCategoryDto.setId(1L);

        ControllerExceptionHandler.ValidationErrorResponse validationErrorResponse = ControllerExceptionHandler.ValidationErrorResponse.builder()
                .status(400)
                .message("Category not exists")
                .errors(List.of("Category not exists"))
                .build();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/categories/" + deleteCategoryDto.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteCategoryDto));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(objectMapper.writeValueAsString(validationErrorResponse), result.getResponse().getContentAsString());
    }
}