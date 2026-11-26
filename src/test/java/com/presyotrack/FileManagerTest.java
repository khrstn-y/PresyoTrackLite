package com.presyotrack;

import com.presyotrack.model.*;
import com.presyotrack.service.FileManager;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

public class FileManagerTest {

    public static void main(String[] args) {

        System.out.println("=== PRESYOTRACK FILE MANAGER TEST ===");

        FileManager fileManager = new FileManager();

        // Test Products
        ArrayList<Product> products = new ArrayList<>();
        products.add(new FoodProduct("Rice", 55, 50));
        products.add(new HouseholdProduct("Soap", 25, 23));

        fileManager.saveProducts(products);

        ArrayList<Product> loadedProducts = fileManager.loadProducts();

        if (loadedProducts.size() != 2) {
            throw new AssertionError("Product save/load failed.");
        }

        System.out.println("Products: PASSED");

        // Test Expenses
        ArrayList<Expense> expenses = new ArrayList<>();
        expenses.add(new Expense(
                "Rice",
                "Groceries",
                500,
                LocalDate.now(),
                "Cash"
        ));

        fileManager.saveExpenses(expenses);

        ArrayList<Expense> loadedExpenses = fileManager.loadExpenses();

        if (loadedExpenses.size() != 1) {
            throw new AssertionError("Expense save/load failed.");
        }

        System.out.println("Expenses: PASSED");

        // Test Budget
        Budget budget = new Budget(5000, YearMonth.now());

        fileManager.saveBudget(budget);

        Budget loadedBudget = fileManager.loadBudget();

        if (loadedBudget == null
                || loadedBudget.getAmount() != 5000) {
            throw new AssertionError("Budget save/load failed.");
        }

        System.out.println("Budget: PASSED");

        // Test Price Records
        ArrayList<PriceRecord> records = new ArrayList<>();
        records.add(new PriceRecord(
                "Rice",
                50,
                55,
                LocalDate.now()
        ));

        fileManager.savePriceRecords(records);

        ArrayList<PriceRecord> loadedRecords =
                fileManager.loadPriceRecords();

        if (loadedRecords.size() != 1) {
            throw new AssertionError("Price record save/load failed.");
        }

        System.out.println("Price Records: PASSED");

        System.out.println("=== ALL FILE TESTS PASSED ===");
    }
}