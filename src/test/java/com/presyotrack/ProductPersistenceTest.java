package com.presyotrack;

import com.presyotrack.model.Product;
import com.presyotrack.model.FoodProduct;
import com.presyotrack.model.HouseholdProduct;
import com.presyotrack.service.ProductManager;

public class ProductPersistenceTest {

    public static void main(String[] args) {

        // Create and save sample products
        ProductManager manager = new ProductManager();

        manager.addProduct(new FoodProduct("Rice", 55.00, 50.00));
        manager.addProduct(new HouseholdProduct("Soap", 35.00, 30.00));

        manager.saveData();

        // Create a new manager and load saved products
        ProductManager newManager = new ProductManager();
        newManager.loadData();

        // Check restored products
        if (newManager.getProducts().size() != 2) {
            throw new AssertionError("Product count is incorrect.");
        }

        Product rice = newManager.searchProduct("Rice");
        Product soap = newManager.searchProduct("Soap");

        if (rice == null || soap == null) {
            throw new AssertionError("Products were not restored.");
        }

        if (!(rice instanceof FoodProduct)) {
            throw new AssertionError("Rice type was not preserved.");
        }

        if (!(soap instanceof HouseholdProduct)) {
            throw new AssertionError("Soap type was not preserved.");
        }

        if (rice.getCurrentPrice() != 55.00) {
            throw new AssertionError("Rice price is incorrect.");
        }

        if (soap.getCurrentPrice() != 35.00) {
            throw new AssertionError("Soap price is incorrect.");
        }

        System.out.println("ALL PRODUCT PERSISTENCE TESTS PASSED!");
    }
}