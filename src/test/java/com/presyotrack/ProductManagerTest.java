
package com.presyotrack;

import com.presyotrack.model.*;
import com.presyotrack.service.ProductManager;
import com.presyotrack.exceptions.InvalidPriceException;

public class ProductManagerTest {

    public static void main(String[] args)
            throws InvalidPriceException {

        System.out.println("=== PRODUCT MANAGER TEST ===");

        ProductManager manager = new ProductManager();

        // Test adding products
        manager.addProduct(new FoodProduct("Rice", 55, 50));
        manager.addProduct(new HouseholdProduct("Soap", 25, 23));

        if (manager.getProducts().size() != 2) {
            throw new AssertionError("Add product failed.");
        }
        System.out.println("Add Products: PASSED");

        // Test searching
        if (manager.searchProduct("Rice") == null) {
            throw new AssertionError("Search product failed.");
        }
        System.out.println("Search Product: PASSED");

        // Test updating price
        manager.updateProductPrice("Rice", 60);

        if (manager.searchProduct("Rice").getCurrentPrice() != 60) {
            throw new AssertionError("Update price failed.");
        }
        System.out.println("Update Product: PASSED");

        // Test category filtering
        if (manager.getProductsByCategory("Food").size() != 1) {
            throw new AssertionError("Category filter failed.");
        }
        System.out.println("Category Filter: PASSED");

        // Test sorting
        if (manager.sortProductsByPrice().get(0).getCurrentPrice() != 25) {
            throw new AssertionError("Price sorting failed.");
        }
        System.out.println("Sort Products: PASSED");

        // Test reports
        if (manager.generateProductReports().size() != 2) {
            throw new AssertionError("Report generation failed.");
        }
        System.out.println("Generate Reports: PASSED");

        // Test deleting
        if (!manager.removeProduct("Soap")) {
            throw new AssertionError("Delete product failed.");
        }
        System.out.println("Delete Product: PASSED");

        // Test counting
        if (manager.countFoodProducts() != 1
                || manager.countHouseholdProducts() != 0) {
            throw new AssertionError("Product counting failed.");
        }
        System.out.println("Product Counting: PASSED");

        System.out.println("=== ALL PRODUCT TESTS PASSED ===");
    }
}
