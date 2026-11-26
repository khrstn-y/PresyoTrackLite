package com.presyotrack;

import com.presyotrack.model.Budget;
import com.presyotrack.model.Expense;
import com.presyotrack.service.BudgetManager;

import java.time.LocalDate;
import java.time.YearMonth;

public class BudgetManagerTest {

    public static void main(String[] args) {

        try {
            Budget budget = new Budget(10000, YearMonth.now());
            BudgetManager manager = new BudgetManager(budget);

            // Test adding an expense
            Expense oldExpense = new Expense(
                    "Rice", "Food", 1500,
                    LocalDate.now(), "Cash"
            );

            manager.addExpense(oldExpense);

            if (manager.getExpenses().size() != 1) {
                throw new AssertionError("Add expense test failed.");
            }

            // Test updating an expense
            Expense newExpense = new Expense(
                    "Rice", "Food", 1800,
                    LocalDate.now(), "Cash"
            );

            boolean updated = manager.updateExpense(
                    oldExpense, newExpense
            );

            if (!updated) {
                throw new AssertionError("Update expense test failed.");
            }

            if (manager.getExpenses().get(0).getAmount() != 1800) {
                throw new AssertionError("Updated amount is incorrect.");
            }

            // Test removing an expense
            manager.removeExpense(newExpense);

            if (!manager.getExpenses().isEmpty()) {
                throw new AssertionError("Remove expense test failed.");
            }

            // Test updating a nonexistent expense
            boolean notUpdated = manager.updateExpense(
                    oldExpense, newExpense
            );

            if (notUpdated) {
                throw new AssertionError(
                        "Nonexistent expense test failed."
                );
            }

            System.out.println("ALL BUDGET MANAGER TESTS PASSED!");

        } catch (Exception e) {
            System.out.println("TEST FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}