package com.nathanlucas.nscommerce.services;

import com.nathanlucas.nscommerce.dtos.ProductDTO;
import com.nathanlucas.nscommerce.dtos.ProductMinDTO;
import com.nathanlucas.nscommerce.entities.Product;
import com.nathanlucas.nscommerce.repositories.ProductRepository;
import com.nathanlucas.nscommerce.services.exceptions.ResourceNotFoundException;
import com.nathanlucas.nscommerce.utils.ProductFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private ModelMapper modelMapper;

    private Long existingId, nonExistingId, dependentId;
    private Product product;
    private ProductDTO productDTO;
    private PageImpl<Product> page;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 4L;
        product = ProductFactory.createProduct();
        productDTO = ProductFactory.createDTO();
        page = new PageImpl<>(List.of(product));

        when(modelMapper.map(product, ProductMinDTO.class)).thenReturn(ProductFactory.createMinDTO());
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        when(repository.findById(existingId)).thenReturn(Optional.of(product));
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
        when(repository.searchByName(anyString(), any(Pageable.class))).thenReturn(page);
        when(repository.save(any(Product.class))).thenReturn(product);
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() {
        ProductDTO result = service.findById(existingId);

        assertNotNull(result);
        assertThat(result).isEqualTo(productDTO);
        assertThat(result.getId()).isEqualTo(existingId);
        assertThat(result.getName()).isEqualTo(product.getName());
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });
    }

    @Test
    public void findAllShouldReturnPageOfProductMinDTO() {
        Page<ProductMinDTO> result = service.findAll("", PageRequest.of(0, 12));

        assertNotNull(result);
        assertThat(result.getSize()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(existingId);
        assertThat(result.getContent().getFirst().getName()).isEqualTo(product.getName());
    }

}
