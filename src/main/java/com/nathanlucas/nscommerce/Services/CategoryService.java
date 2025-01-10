package com.nathanlucas.nscommerce.Services;

import com.nathanlucas.nscommerce.dtos.CategoryDTO;
import com.nathanlucas.nscommerce.entities.Category;
import com.nathanlucas.nscommerce.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private CategoryRepository productRepository;
    private ModelMapper modelMapper;

    public CategoryService(CategoryRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        return productRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private CategoryDTO mapToDTO(Category category) {
        return modelMapper.map(category, CategoryDTO.class);
    }
}
