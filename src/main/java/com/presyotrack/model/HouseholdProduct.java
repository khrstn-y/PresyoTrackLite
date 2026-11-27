package com.presyotrack.model;

import com.presyotrack.interfaces.Reportable;

public class HouseholdProduct extends Product implements Reportable {

    public HouseholdProduct(String name, double currentPrice,
                            double previousPrice) {
        this(name, "Household", currentPrice, previousPrice);
    }

    public HouseholdProduct(String name, String category,
                            double currentPrice, double previousPrice) {
        super(name, category, currentPrice, previousPrice);
    }

    @Override
    public String getProductType() {
        return "Household Product";
    }

    @Override
    public String generateReport() {
        return String.format(
            "Product: %s%nCategory: %s%nCurrent Price: %.2f%nPrevious Price: %.2f%nPrice Difference: %.2f",
            getName(), getCategory(), getCurrentPrice(),
            getPreviousPrice(), getCurrentPrice() - getPreviousPrice()
        );
    }
}