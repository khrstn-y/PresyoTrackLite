package com.presyotrack;

import com.presyotrack.model.Product;
import com.presyotrack.model.FoodProduct;
import com.presyotrack.model.PriceRecord;
import com.presyotrack.service.PriceTracker;

public class PriceTrackerPersistenceTest {

    public static void main(String[] args) {

        // Create a tracker and record a product price
        PriceTracker tracker = new PriceTracker();

        Product rice = new FoodProduct("Rice", 55.00, 50.00);

        tracker.recordPrice(rice);
        tracker.saveData();

        // Create a new tracker and load saved records
        PriceTracker newTracker = new PriceTracker();
        newTracker.loadData();

        // Verify restored records
        if (newTracker.getPriceRecords().size() != 1) {
            throw new AssertionError("Price record count is incorrect.");
        }

        PriceRecord record = newTracker.getPriceRecords().get(0);

        if (!record.getProductName().equals("Rice")) {
            throw new AssertionError("Product name is incorrect.");
        }

        if (record.getPreviousPrice() != 50.00) {
            throw new AssertionError("Previous price is incorrect.");
        }

        if (record.getNewPrice() != 55.00) {
            throw new AssertionError("New price is incorrect.");
        }

        if (record.getPriceDifference() != 5.00) {
            throw new AssertionError("Price difference is incorrect.");
        }

        System.out.println("ALL PRICE TRACKER PERSISTENCE TESTS PASSED!");
    }
}