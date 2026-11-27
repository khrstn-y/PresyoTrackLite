
package com.presyotrack.model;

import java.time.YearMonth;

public class Budget {

    //encapsulations
    private double amount;
    private YearMonth month;

    // Constructor
    public Budget(double amount, YearMonth month) {
        setAmount(amount);
        setMonth(month);
    }

    // Getters
    public double getAmount() {
        return amount;
    }

    public YearMonth getMonth() {
        return month;
    }

    // Setters with validation
    public void setAmount(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException(
                "Budget must be greater than zero."
            );
        }

        this.amount = amount;
    }

    public void setMonth(YearMonth month) {
        if (month == null) {
            throw new IllegalArgumentException(
                "Budget month cannot be empty."
            );
        }

        this.month = month;
    }
}
