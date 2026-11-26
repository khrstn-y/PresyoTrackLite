package com.presyotrack;

import com.presyotrack.model.Budget;
import com.presyotrack.model.Expense;
import com.presyotrack.service.BudgetManager;

import java.time.LocalDate;
import java.time.YearMonth;

public class BudgetPersistenceTest {

    public static void main(String[] args) {

        try {
            // Create and save sample data
            Budget budget = new Budget(10000, YearMonth.now());
            BudgetManager manager = new BudgetManager(budget);

            manager.addExpense(new Expense(
                    "Rice", "Food", 1500,
                    LocalDate.now(), "Cash"
            ));

            manager.addExpense(new Expense(
                    "Electricity", "Utilities", 1200,
                    LocalDate.now(), "Cash"
            ));

            manager.saveData();

            // Create a new manager to simulate restarting the app
            BudgetManager restoredManager = new BudgetManager(
                    new Budget(1, YearMonth.now())
            );

            restoredManager.loadData();

            // Verify restored budget
            if (restoredManager.getBudget().getAmount() != 10000) {
                throw new AssertionError("Budget restoration failed.");
            }

            // Verify restored expenses
            if (restoredManager.getExpenses().size() != 2) {
                throw new AssertionError("Expense restoration failed.");
            }

            if (restoredManager.calculateTotalSpent() != 2700) {
                throw new AssertionError("Expense total restoration failed.");
            }

            System.out.println("ALL BUDGET PERSISTENCE TESTS PASSED!");

        } catch (Exception e) {
            System.out.println("TEST FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}