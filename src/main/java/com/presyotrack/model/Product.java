
package com.presyotrack.model;

public abstract class Product {

    private String name;
    private String category;
    private double currentPrice;
    private double previousPrice;

    // Constructor
    public Product(String name, String category,
                   double currentPrice, double previousPrice) {

        setName(name);
        setCategory(category);
        setCurrentPrice(currentPrice);
        setPreviousPrice(previousPrice);
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getPreviousPrice() {
        return previousPrice;
    }

    // Setters with validation
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }

        this.name = name.trim();
    }

    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty.");
        }

        this.category = category.trim();
    }

    public void setCurrentPrice(double currentPrice) {
        if (!Double.isFinite(currentPrice) || currentPrice < 0) {
            throw new IllegalArgumentException("Invalid current price.");
        }

        this.currentPrice = currentPrice;
    }

    public void setPreviousPrice(double previousPrice) {
        if (!Double.isFinite(previousPrice) || previousPrice < 0) {
            throw new IllegalArgumentException("Invalid previous price.");
        }

        this.previousPrice = previousPrice;
    }

    // Abstract method
    public abstract String getProductType();
}
