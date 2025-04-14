// Package declaration for the Product entity class
package com.example.authsystem.model;

// Importing necessary packages and classes
import jakarta.persistence.*;          // Provides JPA annotations for entity mapping
import java.math.BigDecimal;          // High-precision representation of decimal numbers
import java.time.LocalDateTime;       // Represents date and time without a time zone
import java.util.List;                // Represents a collection of elements
import java.util.UUID;                // Generates universally unique identifiers

// Marks the class as a JPA entity mapped to a database table
@Entity
public class Product {

    // Marks this field as the primary key
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Automatically generates unique values
    private UUID id;  // Unique identifier for the product

    // Specifies that the `name` column should be unique and cannot be null
    @Column(unique = true, nullable = false)
    private String name;  // Name of the product

    // Column for storing product description
    private String description;

    // Specifies that the `price` column cannot be null
    @Column(nullable = false)
    private BigDecimal price;  // Price of the product

    // Field for product discount, defaulting to zero
    private BigDecimal discount = BigDecimal.ZERO;

    // Specifies that the `categoryId` column cannot be null
    @Column(nullable = false)
    private String categoryId;  // Identifier for the category of the product

    // Specifies that the `unitOfMeasure` column cannot be null
    @Column(nullable = false)
    private String unitOfMeasure;  // Unit of measure for the product (e.g., kg, pcs)

    // URL for the product's image
    private String imageUrl;

    // Defines a collection of elements stored as a separate table
    @ElementCollection
    private List<String> tags;  // List of tags associated with the product

    // Specifies that this column is not nullable and cannot be updated after creation
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;  // Timestamp for when the product was created

    // Specifies that the `updatedAt` column cannot be null
    @Column(nullable = false)
    private LocalDateTime updatedAt;  // Timestamp for when the product was last updated

    // Method executed before persisting the entity to set timestamps
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(); // Sets the creation timestamp
        this.updatedAt = LocalDateTime.now(); // Sets the update timestamp
    }

    // Method executed before updating the entity to update the timestamp
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now(); // Updates the update timestamp
    }

    // Getter and setter methods for the `id` field
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    // Getter and setter methods for the `name` field
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter methods for the `description` field
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter and setter methods for the `price` field
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    // Getter and setter methods for the `discount` field
    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    // Getter and setter methods for the `categoryId` field
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    // Getter and setter methods for the `unitOfMeasure` field
    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    // Getter and setter methods for the `imageUrl` field
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Getter and setter methods for the `tags` field
    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    // Getter and setter methods for the `createdAt` field
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Getter and setter methods for the `updatedAt` field
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
