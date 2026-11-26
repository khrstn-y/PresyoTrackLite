package com.presyotrack.service;

import com.presyotrack.model.Budget;
import com.presyotrack.model.Expense;

import java.time.YearMonth;
import java.util.ArrayList;

public class BudgetManager {

    private Budget budget;
    private ArrayList<Expense> expenses;

    // Constructor
    public BudgetManager(Budget budget) {
        this.budget = budget;
        this.expenses = new ArrayList<>();
    }

    // Add expense
    public void addExpense(Expense expense) {
        if (expense == null) {
            throw new IllegalArgumentException("Expense cannot be empty.");
        }

        expenses.add(expense);
    }

    // Remove expense
    public void removeExpense(Expense expense) {
        expenses.remove(expense);
    }

    // Get all expenses
    public ArrayList<Expense> getExpenses() {
        return new ArrayList<>(expenses);
    }

    // Update budget
    public void updateBudget(Budget budget) {
        if (budget == null) {
            throw new IllegalArgumentException("Budget cannot be empty.");
        }

        this.budget = budget;
    }

    // Calculate total expenses for the budget month
    public double calculateTotalSpent() {
        double total = 0;
        YearMonth selectedMonth = budget.getMonth();

        for (Expense expense : expenses) {
            YearMonth expenseMonth = YearMonth.from(expense.getDate());

            if (expenseMonth.equals(selectedMonth)) {
                total += expense.getAmount();
            }
        }

        return total;
    }

    // Calculate remaining budget
    public double calculateRemainingBudget() {
        return budget.getAmount() - calculateTotalSpent();
    }

    // Get expenses by category
    public ArrayList<Expense> getExpensesByCategory(String category) {
        ArrayList<Expense> filteredExpenses = new ArrayList<>();

        for (Expense expense : expenses) {
            if (expense.getCategory().equalsIgnoreCase(category)) {
                filteredExpenses.add(expense);
            }
        }

        return filteredExpenses;
    }
}