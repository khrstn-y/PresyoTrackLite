package com.presyotrack;

import com.presyotrack.model.FoodProduct;
import com.presyotrack.model.Budget;
import com.presyotrack.service.BudgetManager;
import com.presyotrack.exceptions.InvalidPriceException;
import com.presyotrack.exceptions.InvalidBudgetException;

import java.time.YearMonth;

public class ExceptionTest {

    public static void main(String[] args) {

        System.out.println("=== PRESYOTRACK EXCEPTION TEST ===");

        // Test 1: Invalid Price Exception
        FoodProduct product = new FoodProduct("Rice", 50, 45);

        try {
            product.updateCurrentPrice(-10);
        } catch (InvalidPriceException e) {
            System.out.println("Price Error: " + e.getMessage());
        } finally {
            System.out.println("Price validation finished.");
        }

        // Test 2: Invalid Budget Exception
        Budget budget = new Budget(5000, YearMonth.now());
        BudgetManager manager = new BudgetManager(budget);

        try {
            manager.updateBudgetAmount(-1000);
        } catch (InvalidBudgetException e) {
            System.out.println("Budget Error: " + e.getMessage());
        } finally {
            System.out.println("Budget validation finished.");
        }

        System.out.println("=== TEST COMPLETED ===");
    }
}