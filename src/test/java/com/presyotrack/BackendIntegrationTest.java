package com.presyotrack;

import com.presyotrack.model.*;
import com.presyotrack.service.*;

import java.time.LocalDate;
import java.time.YearMonth;

public class BackendIntegrationTest {

    public static void main(String[] args) throws Exception {

        // 1. PRODUCT MANAGEMENT
        ProductManager productManager = new ProductManager();

        FoodProduct rice = new FoodProduct("Integration Rice", 55.00, 50.00);

        productManager.addProduct(rice);
        productManager.saveData();

        ProductManager loadedProducts = new ProductManager();
        loadedProducts.loadData();

        if (loadedProducts.searchProduct("Integration Rice") == null) {
            throw new AssertionError("Product integration failed.");
        }

        // 2. PRICE TRACKING
        PriceTracker priceTracker = new PriceTracker();

        priceTracker.recordPrice(rice);
        priceTracker.saveData();

        PriceTracker loadedPrices = new PriceTracker();
        loadedPrices.loadData();

        if (loadedPrices.getPriceHistory("Integration Rice").isEmpty()) {
            throw new AssertionError("Price tracking integration failed.");
        }

        // 3. BUDGET AND EXPENSE MANAGEMENT
        Budget budget = new Budget(10000.00, YearMonth.now());

        BudgetManager budgetManager = new BudgetManager(budget);

        Expense expense = new Expense(
                "Integration Groceries",
                "Food",
                1500.00,
                LocalDate.now(),
                "Cash"
        );

        budgetManager.addExpense(expense);
        budgetManager.saveData();

        BudgetManager loadedBudget =
                new BudgetManager(new Budget(1.00, YearMonth.now()));

        loadedBudget.loadData();

        if (loadedBudget.getExpenses().size() != 1) {
            throw new AssertionError("Expense integration failed.");
        }

        if (loadedBudget.calculateTotalSpent() != 1500.00) {
            throw new AssertionError("Budget calculation integration failed.");
        }

        // 4. FINAL VERIFICATION
        System.out.println("PRODUCT MANAGEMENT: PASSED");
        System.out.println("PRICE TRACKING: PASSED");
        System.out.println("BUDGET MANAGEMENT: PASSED");
        System.out.println("FILE PERSISTENCE: PASSED");

        System.out.println("\nALL BACKEND INTEGRATION TESTS PASSED!");
    }
}