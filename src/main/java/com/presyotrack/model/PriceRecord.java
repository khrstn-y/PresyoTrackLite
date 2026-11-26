
package com.presyotrack.model;

import java.time.LocalDate;

public class PriceRecord {

    private String productName;
    private double previousPrice;
    private double newPrice;
    private LocalDate dateRecorded;

    // Constructor
    public PriceRecord(String productName, double previousPrice,
                       double newPrice, LocalDate dateRecorded) {

        setProductName(productName);
        setPreviousPrice(previousPrice);
        setNewPrice(newPrice);
        setDateRecorded(dateRecorded);
    }

    // Getters
    public String getProductName() {
        return productName;
    }

    public double getPreviousPrice() {
        return previousPrice;
    }

    public double getNewPrice() {
        return newPrice;
    }

    public LocalDate getDateRecorded() {
        return dateRecorded;
    }

    // Setters with validation
    public void setProductName(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Product name cannot be empty."
            );
        }

        this.productName = productName.trim();
    }

    public void setPreviousPrice(double previousPrice) {
        if (!Double.isFinite(previousPrice) || previousPrice < 0) {
            throw new IllegalArgumentException(
                "Invalid previous price."
            );
        }

        this.previousPrice = previousPrice;
    }

    public void setNewPrice(double newPrice) {
        if (!Double.isFinite(newPrice) || newPrice < 0) {
            throw new IllegalArgumentException(
                "Invalid new price."
            );
        }

        this.newPrice = newPrice;
    }

    public void setDateRecorded(LocalDate dateRecorded) {
        if (dateRecorded == null) {
            throw new IllegalArgumentException(
                "Date recorded cannot be empty."
            );
        }

        this.dateRecorded = dateRecorded;
    }

    // Calculates the price difference
    public double getPriceDifference() {
        return newPrice - previousPrice;
    }
}
