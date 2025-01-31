package com.nathanlucas.nscommerce.services;

import com.nathanlucas.nscommerce.dtos.CategoryDTO;
import com.nathanlucas.nscommerce.entities.Category;
import com.nathanlucas.nscommerce.repositories.CategoryRepository;
import com.nathanlucas.nscommerce.utils.CategoryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTests {

    @InjectMocks
    private CategoryService service;

    @Mock
    private CategoryRepository repository;

    @Mock
    private ModelMapper modelMapper;

    private Category category;
    private CategoryDTO categoryDTO;
    private List<Category> list;

    @BeforeEach
    void setUp() throws Exception {
        category = CategoryFactory.createCategory();
        categoryDTO = CategoryFactory.createDTO();
        list = new ArrayList<>();
        list.add(category);

        when(repository.findAll()).thenReturn(list);
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(categoryDTO);
    }

    @Test
    public void findAllShouldReturnListOfCategoryDTO() {
        List<CategoryDTO> result = service.findAll();

        assertThat(result).isNotNull();
        assertEquals(result.size(), 1);
        assertEquals(result.getFirst().getId(), category.getId());
        assertEquals(result.getFirst().getName(), category.getName());
    }
}
