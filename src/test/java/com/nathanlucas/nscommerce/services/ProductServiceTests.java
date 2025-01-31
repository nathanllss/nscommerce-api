package com.nathanlucas.nscommerce.services;

import com.nathanlucas.nscommerce.dtos.ProductDTO;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 4L;
        product = ProductFactory.createProduct();
        productDTO = ProductFactory.createDTO();

        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        when(repository.findById(existingId)).thenReturn(Optional.of(product));
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
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

}
