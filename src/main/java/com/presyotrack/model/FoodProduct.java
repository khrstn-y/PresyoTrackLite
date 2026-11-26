
package com.presyotrack.model;

public class FoodProduct extends Product {

    // Constructor
    public FoodProduct(String name, double currentPrice,
                       double previousPrice) {

        super(name, "Food", currentPrice, previousPrice);
    }

    // Overridden method
    @Override
    public String getProductType() {
        return "Food Product";
    }
}
