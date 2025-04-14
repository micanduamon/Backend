// Package declaration for the ProductController class
package com.example.authsystem.controller;

// Import necessary classes and annotations for the controller
import com.example.authsystem.dto.ProductDTO;
import com.example.authsystem.model.Product;
import com.example.authsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;               // Represents a collection of elements
import java.util.UUID;               // For unique product identifiers
import java.util.stream.Collectors;  // To convert a list of entities to DTOs

// Marks this class as a REST controller that handles HTTP requests
@RestController
@RequestMapping("/api/products") // Base URL mapping for all product-related endpoints
public class ProductController {

    // Dependency on the ProductService for business logic
    private final ProductService productService;

    // Constructor-based dependency injection
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Endpoint to add a new product
    @PostMapping
    public ResponseEntity<ProductDTO> addProduct(@RequestBody ProductDTO productDTO) {
        Product product = toEntity(productDTO);                 // Convert DTO to entity
        Product savedProduct = productService.addProduct(product); // Save the product
        return new ResponseEntity<>(toDTO(savedProduct), HttpStatus.CREATED); // Return the created product
    }

    // Endpoint to fetch all products
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<Product> products = productService.getAllProducts(); // Fetch all products
        List<ProductDTO> productDTOS = products.stream()          // Convert entities to DTOs
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(productDTOS, HttpStatus.OK);  // Return the list of products
    }

    // Endpoint to fetch a product by its ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable UUID id) {
        Product product = productService.getProductById(id);      // Fetch product by ID
        return new ResponseEntity<>(toDTO(product), HttpStatus.OK); // Return the product
    }

    // Endpoint to fetch products by category ID
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable String categoryId) {
        List<Product> products = productService.getProductsByCategory(categoryId); // Fetch products by category
        List<ProductDTO> productDTOS = products.stream()                           // Convert entities to DTOs
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(productDTOS, HttpStatus.OK);                   // Return the list of products
    }

    // Endpoint to delete a product by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);                      // Delete the product by ID
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);    // Return a 204 (No Content) response
    }

    // Converts a Product entity to a ProductDTO
    private ProductDTO toDTO(Product product) {
        if (product == null) {
            return null; // Return null if the entity is null
        }
        ProductDTO dto = new ProductDTO();                     // Create a new DTO instance
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setDiscount(product.getDiscount());
        dto.setCategoryId(product.getCategoryId());
        dto.setUnitOfMeasure(product.getUnitOfMeasure());
        dto.setImageUrl(product.getImageUrl());
        dto.setTags(product.getTags());
        return dto;                                            // Return the populated DTO
    }

    // Converts a ProductDTO to a Product entity
    private Product toEntity(ProductDTO productDTO) {
        if (productDTO == null) {
            return null; // Return null if the DTO is null
        }
        Product product = new Product();                      // Create a new entity instance
        product.setId(productDTO.getId());
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setDiscount(productDTO.getDiscount());
        product.setCategoryId(productDTO.getCategoryId());
        product.setUnitOfMeasure(productDTO.getUnitOfMeasure());
        product.setImageUrl(productDTO.getImageUrl());
        product.setTags(productDTO.getTags());
        return product;                                       // Return the populated entity
    }
}

