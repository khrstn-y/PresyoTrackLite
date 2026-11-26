
package com.presyotrack;

import com.presyotrack.model.FoodProduct;
import com.presyotrack.model.PriceRecord;
import com.presyotrack.service.PriceTracker;

public class PriceTrackerTest {

    public static void main(String[] args) {

        System.out.println("=== PRICE TRACKER TEST ===");

        PriceTracker tracker = new PriceTracker();

        // Test recording prices
        FoodProduct rice = new FoodProduct("Rice", 55, 50);
        tracker.recordPrice(rice);

        if (tracker.getPriceRecords().size() != 1) {
            throw new AssertionError("Price recording failed.");
        }
        System.out.println("Record Price: PASSED");

        // Test price history
        if (tracker.getPriceHistory("Rice").size() != 1) {
            throw new AssertionError("Price history failed.");
        }
        System.out.println("Price History: PASSED");

        // Test price difference
        if (tracker.getPriceDifference("Rice") != 5) {
            throw new AssertionError("Price difference failed.");
        }
        System.out.println("Price Difference: PASSED");

        // Test price status
        if (!tracker.getPriceStatus("Rice").equals("Price Increased")) {
            throw new AssertionError("Price status failed.");
        }
        System.out.println("Price Status: PASSED");

        // Test percentage
        if (tracker.getPriceChangePercentage("Rice") != 10) {
            throw new AssertionError("Percentage calculation failed.");
        }
        System.out.println("Price Percentage: PASSED");

        // Test sorting
        PriceRecord latest = tracker.sortRecordsByDate().get(0);

        if (!latest.getProductName().equals("Rice")) {
            throw new AssertionError("Sorting failed.");
        }
        System.out.println("Sort Records: PASSED");

        // Test summary
        if (!tracker.generatePriceSummary("Rice").contains("Price Increased")) {
            throw new AssertionError("Price summary failed.");
        }
        System.out.println("Price Summary: PASSED");

        // Test deletion
        if (!tracker.removePriceHistory("Rice")) {
            throw new AssertionError("Price history deletion failed.");
        }
        System.out.println("Delete History: PASSED");

        System.out.println("=== ALL PRICE TRACKER TESTS PASSED ===");
    }
}
