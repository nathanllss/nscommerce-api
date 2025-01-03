package com.nathanlucas.nscommerce.Services;

import com.nathanlucas.nscommerce.Services.exceptions.DatabaseException;
import com.nathanlucas.nscommerce.Services.exceptions.ResourceNotFoundException;
import com.nathanlucas.nscommerce.dtos.ProductDTO;
import com.nathanlucas.nscommerce.entities.Product;
import com.nathanlucas.nscommerce.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
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
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));
    }

    @Transactional(readOnly = true)
    public Page<Product> findAll(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products;
    }

    @Transactional
    public Product insert(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, Product updatedProduct) {
        try {
            Product entity = productRepository.getReferenceById(id);
            modelMapper.map(updatedProduct, entity);
            entity.setId(id);
            return productRepository.save(entity);
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
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

}
