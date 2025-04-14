// Package declaration for the service class
package com.example.authsystem.service;

// Import necessary classes and annotations
import com.example.authsystem.model.Category;
import com.example.authsystem.exception.ProductNotFoundException;
import com.example.authsystem.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

// Service class that handles business logic related to categories
@Service
public class CategoryService {

    // Injected repository for database operations on Category entities
    private final CategoryRepository categoryRepository;

    // Constructor-based dependency injection for CategoryRepository
    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Method to add a new category to the database
    public Category addCategory(Category category) {
        // Saves the category and returns the saved entity
        return categoryRepository.save(category);
    }

    // Method to fetch all categories from the database
    public List<Category> getAllCategories() {
        // Retrieves all categories as a list
        return categoryRepository.findAll();
    }

    // Method to fetch a specific category by its ID
    public Category getCategoryById(UUID id) {
        // If the category is not found, throws a custom ProductNotFoundException
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Category not found with ID: " + id));
    }

    // Method to delete a category by its ID
    public void deleteCategory(UUID id) {
        // Deletes the category using its ID
        categoryRepository.deleteById(id);
    }
}
