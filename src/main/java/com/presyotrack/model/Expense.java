
package com.presyotrack.model;

import java.time.LocalDate;

public class Expense {

    private String name;
    private String category;
    private double amount;
    private LocalDate date;
    private String payment;

    // Constructor
    public Expense(String name, String category, double amount,
                   LocalDate date, String payment) {

        setName(name);
        setCategory(category);
        setAmount(amount);
        setDate(date);
        setPayment(payment);
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getPayment() {
        return payment;
    }

    // Setters with validation
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Expense name cannot be empty.");
        }

        this.name = name.trim();
    }

    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty.");
        }

        this.category = category.trim();
    }

    public void setAmount(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        this.amount = amount;
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be empty.");
        }

        this.date = date;
    }

    public void setPayment(String payment) {
        if (payment == null || payment.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment method cannot be empty.");
        }

        this.payment = payment.trim();
    }
}
