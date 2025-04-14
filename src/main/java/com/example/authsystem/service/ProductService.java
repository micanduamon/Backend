// Package declaration for the service class
package com.example.authsystem.service;

// Import necessary classes and annotations
import com.example.authsystem.model.Product;
import com.example.authsystem.exception.ProductNotFoundException;
import com.example.authsystem.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Service class that handles business logic related to products
@Service
public class ProductService {

    // Injected repository for database operations on Product entities
    private final ProductRepository productRepository;

    // Constructor-based dependency injection for ProductRepository
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Method to add a new product to the database
    public Product addProduct(Product product) {
        // Saves the product and returns the saved entity
        return productRepository.save(product);
    }

    // Method to fetch all products from the database
    public List<Product> getAllProducts() {
        // Retrieves all products using the custom query in the repository
        return productRepository.findAllProducts();
    }

    // Method to fetch a specific product by its ID
    public Product getProductById(UUID id) {
        // If the product is not found, throws a custom ProductNotFoundException
        Optional<Product> productOptional = productRepository.findProductById(id);
        return productOptional.orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
    }

    // Method to fetch products by category ID
    public List<Product> getProductsByCategory(String categoryId) {
        // Retrieves all products associated with the given category ID
        return productRepository.findByCategoryId(categoryId);
    }

    // Method to delete a product by its ID
    public void deleteProduct(UUID id) {
        // Deletes the product using its ID
        productRepository.deleteById(id);
    }
}




