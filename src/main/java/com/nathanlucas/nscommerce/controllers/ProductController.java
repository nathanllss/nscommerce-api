package com.nathanlucas.nscommerce.controllers;

import com.nathanlucas.nscommerce.Services.ProductService;
import com.nathanlucas.nscommerce.dtos.ProductDTO;
import com.nathanlucas.nscommerce.dtos.ProductMinDTO;
import com.nathanlucas.nscommerce.entities.Product;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ModelMapper modelMapper;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CLIENT')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
        Product result = productService.findById(id);
        ProductDTO dto = mapToDTO(result);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<Page<ProductMinDTO>> findAll(@RequestParam(name = "name", defaultValue = "") String name, Pageable pageable) {
        Page<ProductMinDTO> result = productService.findAll(name, pageable).map(this::mapToMinDTO);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> create(@Valid @RequestBody ProductDTO productDTO) {
        Product product = mapToEntity(productDTO);
        product = productService.insert(product);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(product.getId()).toUri();
        return ResponseEntity.created(uri).body(mapToDTO(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        Product product = mapToEntity(productDTO);
        product = productService.update(id, product);
        return ResponseEntity.ok(mapToDTO(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ProductDTO mapToDTO(Product product) {
        return modelMapper.map(product, ProductDTO.class);
    }
    private ProductMinDTO mapToMinDTO(Product product) {
        return modelMapper.map(product, ProductMinDTO.class);
    }

    private Product mapToEntity(ProductDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }
}
