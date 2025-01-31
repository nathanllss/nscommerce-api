package com.nathanlucas.nscommerce.utils;

import com.nathanlucas.nscommerce.dtos.CategoryDTO;
import com.nathanlucas.nscommerce.dtos.ProductDTO;
import com.nathanlucas.nscommerce.dtos.ProductMinDTO;
import com.nathanlucas.nscommerce.entities.Category;
import com.nathanlucas.nscommerce.entities.Product;

public class ProductFactory {

    public static ProductDTO createDTO() {
        ProductDTO productDTO = new ProductDTO(
                1L, "Console PlayStation 5",
                "Descrição produto",
                4999.99, "url do produto"
        );
        CategoryDTO cat = CategoryFactory.createDTO();
        productDTO.getCategories().add(cat);
        return productDTO;
    }
    public static ProductDTO createDTO(String name) {
        ProductDTO productDTO = createDTO();
        productDTO.setName(name);
        return productDTO;
    }
    public static Product createProduct() {
        Category cat = CategoryFactory.createCategory();
        Product product = new Product(
                1L, "Console PlayStation 5",
                "Descrição produto",
                4999.99, "url do produto"
        );
        product.getCategories().add(cat);
        return product;
    }

    public static Product createProduct(String name) {
        Product product = createProduct();
        product.setName(name);
        return product;
    }

    public static ProductMinDTO createMinDTO() {
        return new ProductMinDTO(1L, "Console PlayStation 5",
                4999.99, "url do produto"
        );
    }
}
