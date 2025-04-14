// Package declaration for the ProductDTO class
package com.example.authsystem.dto;

// Importing required classes for the code
import java.math.BigDecimal; // Provides support for high-precision decimal numbers
import java.util.List;       // Represents a collection of elements (a list)
import java.util.UUID;       // Provides universally unique identifiers

// Public class definition for a Data Transfer Object (DTO) named ProductDTO
public class ProductDTO {
    // Unique identifier for the product
    private UUID id;

    // Name of the product
    private String name;

    // Description of the product
    private String description;

    // Price of the product
    private BigDecimal price;

    // Discount applied to the product price
    private BigDecimal discount;

    // Identifier for the category to which the product belongs
    private String categoryId;

    // Unit of measure for the product (e.g., kg, pcs, etc.)
    private String unitOfMeasure;

    // URL of the product's image
    private String imageUrl;

    // List of tags associated with the product
    private List<String> tags;

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

    // Getter method for the `price` field
    public BigDecimal getPrice() {
        return price;
    }

    // Setter method for the `price` field
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    // Getter method for the `discount` field
    public BigDecimal getDiscount() {
        return discount;
    }

    // Setter method for the `discount` field
    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    // Getter method for the `categoryId` field
    public String getCategoryId() {
        return categoryId;
    }

    // Setter method for the `categoryId` field
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    // Getter method for the `unitOfMeasure` field
    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    // Setter method for the `unitOfMeasure` field
    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    // Getter method for the `imageUrl` field
    public String getImageUrl() {
        return imageUrl;
    }

    // Setter method for the `imageUrl` field
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Getter method for the `tags` field
    public List<String> getTags() {
        return tags;
    }

    // Setter method for the `tags` field
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
