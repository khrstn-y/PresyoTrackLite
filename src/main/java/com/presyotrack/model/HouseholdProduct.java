
package com.presyotrack.model;

public class HouseholdProduct extends Product {

    // Constructor
    public HouseholdProduct(String name, double currentPrice,
                            double previousPrice) {

        super(name, "Household", currentPrice, previousPrice);
    }

    // Overridden method
    @Override
    public String getProductType() {
        return "Household Product";
    }
}
