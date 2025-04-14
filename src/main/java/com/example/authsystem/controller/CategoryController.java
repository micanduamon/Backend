// Package declaration for the CategoryController class
package com.example.authsystem.controller;

// Importing necessary classes and annotations for the controller
import com.example.authsystem.dto.CategoryDTO;
import com.example.authsystem.model.Category;
import com.example.authsystem.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;               // Represents a collection of elements
import java.util.UUID;               // Provides universally unique identifiers
import java.util.stream.Collectors;  // For converting collections

// Marks this class as a REST controller that handles HTTP requests
@RestController
@RequestMapping("/api/categories") // Base URL for category-related endpoints
public class CategoryController {

    // Dependency on the CategoryService for business logic
    private final CategoryService categoryService;

    // Constructor-based dependency injection
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Endpoint to add a new category
    @PostMapping
    public ResponseEntity<CategoryDTO> addCategory(@RequestBody CategoryDTO categoryDTO) {
        // Convert the DTO to an entity and save it
        Category savedCategory = categoryService.addCategory(categoryDTO.toEntity());
        // Return the saved category as a DTO with a 201 (Created) status
        return new ResponseEntity<>(toDTO(savedCategory), HttpStatus.CREATED);
    }

    // Endpoint to retrieve all categories
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        // Fetch all categories
        List<Category> categories = categoryService.getAllCategories();
        // Convert each entity to a DTO and collect into a list
        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        // Return the list of categories with a 200 (OK) status
        return new ResponseEntity<>(categoryDTOS, HttpStatus.OK);
    }

    // Endpoint to retrieve a category by its ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable UUID id) {
        // Fetch the category by its ID
        Category category = categoryService.getCategoryById(id);
        // Return the category as a DTO with a 200 (OK) status
        return new ResponseEntity<>(toDTO(category), HttpStatus.OK);
    }

    // Endpoint to delete a category by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        // Delete the category using its ID
        categoryService.deleteCategory(id);
        // Return a 204 (No Content) status
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Converts a Category entity to a CategoryDTO
    private CategoryDTO toDTO(Category category) {
        // Create a new DTO instance
        CategoryDTO categoryDTO = new CategoryDTO();
        // Populate the DTO fields with the entity's values
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        categoryDTO.setDescription(category.getDescription());
        // Return the populated DTO
        return categoryDTO;
    }
}
