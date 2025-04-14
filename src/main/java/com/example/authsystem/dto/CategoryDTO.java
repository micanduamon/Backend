// Package declaration for the CategoryDTO class
package com.example.authsystem.dto;

// Importing the Category entity class
import com.example.authsystem.model.Category;
import java.util.UUID; // Provides universally unique identifiers

// Data Transfer Object (DTO) for the Category entity
public class CategoryDTO {

    // Unique identifier for the category
    private UUID id;

    // Name of the category
    private String name;

    // Description of the category
    private String description;

    // Default constructor (required for serialization/deserialization frameworks)
    public CategoryDTO() {}

    // Constructor to initialize the DTO using a Category entity
    public CategoryDTO(Category category) {
        // Mapping fields from the entity to the DTO
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
    }

    // Converts this DTO back to a Category entity
    public Category toEntity() {
        Category category = new Category(); // Create a new Category instance
        category.setId(this.id);           // Set the `id` field from the DTO
        category.setName(this.name);       // Set the `name` field from the DTO
        category.setDescription(this.description); // Set the `description` field
        return category;                   // Return the populated entity
    }

    // Getter method for the `id` field
    public UUID getId() {
        return id;
    }

    // Setter method for the `id` field
    public void setId(UUID id) {
        this.id = id;
    }

    // Getter method for the `name` field
    public String getName() {
        return name;
    }

    // Setter method for the `name` field
    public void setName(String name) {
        this.name = name;
    }

    // Getter method for the `description` field
    public String getDescription() {
        return description;
    }

    // Setter method for the `description` field
    public void setDescription(String description) {
        this.description = description;
    }
}
