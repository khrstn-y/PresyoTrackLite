package com.presyotrack.service;

import com.presyotrack.interfaces.Reportable;
import com.presyotrack.model.Budget;
import com.presyotrack.model.Expense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BudgetReport implements Reportable {

    private BudgetManager budgetManager;

    // Constructor
    public BudgetReport(BudgetManager budgetManager) {

        if (budgetManager == null) {
            throw new IllegalArgumentException(
                    "Budget manager cannot be null."
            );
        }

        this.budgetManager = budgetManager;
    }

    // Get total expenses
    public double getTotalExpenses() {
        return budgetManager.calculateTotalSpent();
    }

    // Get remaining budget
    public double getRemainingBudget() {
        return budgetManager.calculateRemainingBudget();
    }

    // Calculate budget utilization
    public double getBudgetUtilization() {

        Budget budget = budgetManager.getBudget();

        if (budget.getAmount() == 0) {
            return 0;
        }

        return (getTotalExpenses() / budget.getAmount()) * 100;
    }

    // Group expenses by category
    public HashMap<String, Double> getExpensesByCategory() {

        HashMap<String, Double> categoryTotals = new HashMap<>();

        ArrayList<Expense> expenses = budgetManager.getExpenses();

        for (Expense expense : expenses) {

            String category = expense.getCategory();

            categoryTotals.put(
                    category,
                    categoryTotals.getOrDefault(category, 0.0)
                            + expense.getAmount()
            );
        }

        return categoryTotals;
    }

    // Check budget status
    public String getBudgetStatus() {

        if (getRemainingBudget() < 0) {
            return "Over Budget";
        }

        if (getRemainingBudget() == 0) {
            return "Budget Fully Used";
        }

        return "Within Budget";
    }

    // Generate complete budget report
    @Override
    public String generateReport() {

        StringBuilder report = new StringBuilder();

        report.append("===== PRESYOTRACK BUDGET REPORT =====\n");

        report.append(String.format(
                "Total Expenses: %.2f%n",
                getTotalExpenses()
        ));

        report.append(String.format(
                "Remaining Budget: %.2f%n",
                getRemainingBudget()
        ));

        report.append(String.format(
                "Budget Utilization: %.2f%%%n",
                getBudgetUtilization()
        ));

        report.append("Budget Status: ")
                .append(getBudgetStatus())
                .append("\n");

        report.append("\nEXPENSES BY CATEGORY:\n");

        for (Map.Entry<String, Double> entry :
                getExpensesByCategory().entrySet()) {

            report.append(String.format(
                    "%s: %.2f%n",
                    entry.getKey(),
                    entry.getValue()
            ));
        }

        return report.toString();
    }
    // Get current budget
    public Budget getCurrentBudget() {
        return budgetManager.getBudget();
    }
}

