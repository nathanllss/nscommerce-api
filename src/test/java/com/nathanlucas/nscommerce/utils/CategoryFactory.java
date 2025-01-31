package com.nathanlucas.nscommerce.utils;

import com.nathanlucas.nscommerce.dtos.CategoryDTO;
import com.nathanlucas.nscommerce.entities.Category;

public class CategoryFactory {

    public static CategoryDTO createDTO() {
        return new CategoryDTO(1L, "Games");
    }
    public static CategoryDTO createDTO(Long id, String name) {
        return new CategoryDTO(id, name);
    }
    public static Category createCategory() {
        return new Category(1L, "Games");
    }

    public static Category createCategory(Long id, String name) {
        return new Category(id, name);
    }
}
