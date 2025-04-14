// Package declaration for the Category entity class
package com.example.authsystem.model;

// Importing necessary classes and annotations for JPA entity mapping
import jakarta.persistence.*;
import java.util.UUID;  // Provides universally unique identifiers

// Marks this class as a JPA entity mapped to a database table
@Entity
public class Category {

    // Marks the `id` field as the primary key and specifies that its value is generated automatically
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;  // Unique identifier for the category

    // Specifies that the `name` column must be unique and cannot be null
    @Column(unique = true, nullable = false)
    private String name;  // Name of the category

    // Column for storing an optional description of the category
    private String description;

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





