package com.nathanlucas.nscommerce.controllers;

import com.nathanlucas.nscommerce.Services.CategoryService;
import com.nathanlucas.nscommerce.dtos.CategoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> findAll() {
        List<CategoryDTO> result = categoryService.findAll();
        return ResponseEntity.ok(result);
    }
}
