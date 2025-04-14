// Package declaration for the repository class
package com.example.authsystem.repository;

// Import the Category entity and JpaRepository interface
import com.example.authsystem.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

// Repository interface for performing CRUD operations on Category entities
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    // JpaRepository provides built-in methods for common database operations,
    // such as save, findById, findAll, delete, etc.

    // Additional custom query methods can be added here as needed
}
