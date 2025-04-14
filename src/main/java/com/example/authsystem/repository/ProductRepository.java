// Package declaration for the repository class
package com.example.authsystem.repository;

// Import required classes and interfaces
import com.example.authsystem.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Repository interface for performing CRUD and custom operations on Product entities
public interface ProductRepository extends JpaRepository<Product, UUID> {

    // Custom query to fetch products by categoryId, including their tags
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.tags WHERE p.categoryId = :categoryId")
    List<Product> findByCategoryId(String categoryId);
    // This query retrieves products that belong to a specific category
    // Uses LEFT JOIN FETCH to eagerly fetch the associated tags for better performance

    // Custom query to fetch a single product by its ID, including its tags
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.tags WHERE p.id = :id")
    Optional<Product> findProductById(UUID id);
    // Returns an Optional containing the product if found, or empty if not
    // Ensures tags are fetched along with the product data

    // Custom query to fetch all products, including their tags
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.tags")
    List<Product> findAllProducts();
    // Retrieves all products with tags eagerly loaded to avoid lazy loading issues
}



