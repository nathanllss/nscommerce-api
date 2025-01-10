package com.nathanlucas.nscommerce.Services;

import com.nathanlucas.nscommerce.Services.exceptions.DatabaseException;
import com.nathanlucas.nscommerce.Services.exceptions.ResourceNotFoundException;
import com.nathanlucas.nscommerce.dtos.CategoryDTO;
import com.nathanlucas.nscommerce.dtos.ProductDTO;
import com.nathanlucas.nscommerce.dtos.ProductMinDTO;
import com.nathanlucas.nscommerce.entities.Category;
import com.nathanlucas.nscommerce.entities.Product;
import com.nathanlucas.nscommerce.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private ProductRepository productRepository;
    private ModelMapper modelMapper;

    public ProductService(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));
        return mapToDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<ProductMinDTO> findAll(String name, Pageable pageable) {
        Page<ProductMinDTO> products = productRepository.searchByName(name, pageable).map(this::mapToMinDTO);
        return products;
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product entity = mapToEntity(dto);
        entity = productRepository.save(entity);
        return mapToDTO(entity);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO updatedProduct) {
        try {
            Product entity = productRepository.getReferenceById(id);
            modelMapper.map(updatedProduct, entity);
            entity.setId(id);
            entity = productRepository.save(entity);
            return mapToDTO(entity);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try {
            productRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    private ProductDTO mapToDTO(Product product) {
        ProductDTO dto = modelMapper.map(product, ProductDTO.class);
        for (Category cat : product.getCategories()) {
            CategoryDTO catDTO = new CategoryDTO(cat);
            dto.getCategories().add(catDTO);
        }
        return dto;
    }
    private ProductMinDTO mapToMinDTO(Product product) {
        return modelMapper.map(product, ProductMinDTO.class);
    }

    private Product mapToEntity(ProductDTO productDTO) {
        Product entity = modelMapper.map(productDTO, Product.class);
        for (CategoryDTO catDTO : productDTO.getCategories()) {
            Category cat = new Category();
            cat.setId(catDTO.getId());
            entity.getCategories().add(cat);
        }
        return entity;
    }

}
