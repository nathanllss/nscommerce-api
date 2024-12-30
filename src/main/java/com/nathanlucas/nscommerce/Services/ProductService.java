package com.nathanlucas.nscommerce.Services;

import com.nathanlucas.nscommerce.entities.Product;
import com.nathanlucas.nscommerce.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id).get();
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
}
