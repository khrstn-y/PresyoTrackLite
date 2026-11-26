package com.presyotrack.service;

import com.presyotrack.model.Budget;
import com.presyotrack.model.Expense;
import com.presyotrack.exceptions.InvalidBudgetException;

import java.time.YearMonth;
import java.util.ArrayList;

public class BudgetManager {

    private Budget budget;
    private ArrayList<Expense> expenses;
    private FileManager fileManager;

    // Constructor
    public BudgetManager(Budget budget) {
        if (budget == null) {
            throw new IllegalArgumentException("Budget cannot be empty.");
        }

        this.budget = budget;
        this.expenses = new ArrayList<>();
        this.fileManager = new FileManager();
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

    // Update an existing expense
    public boolean updateExpense(Expense oldExpense, Expense newExpense) {
        if (oldExpense == null || newExpense == null) {
            throw new IllegalArgumentException("Expense cannot be empty.");
        }

        int index = expenses.indexOf(oldExpense);

        if (index == -1) {
            return false;
        }

        expenses.set(index, newExpense);
        return true;
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

    // Update budget amount using custom exception
    public void updateBudgetAmount(double amount)
            throws InvalidBudgetException {

        if (!Double.isFinite(amount) || amount <= 0) {
            throw new InvalidBudgetException(
                    "Budget must be greater than zero."
            );
        }

        budget.setAmount(amount);
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

        if (category == null || category.trim().isEmpty()) {
            return filteredExpenses;
        }

        for (Expense expense : expenses) {
            if (expense.getCategory().equalsIgnoreCase(category.trim())) {
                filteredExpenses.add(expense);
            }
        }

        return filteredExpenses;
    }

    // Get current budget
    public Budget getBudget() {
        return budget;
    }

    // Load saved budget and expenses from files
    public void loadData() {
        Budget savedBudget = fileManager.loadBudget();

        if (savedBudget != null) {
            this.budget = savedBudget;
        }

        expenses.clear();
        expenses.addAll(fileManager.loadExpenses());
    }

    // Save current budget and expenses to files
    public void saveData() {
        fileManager.saveBudget(budget);
        fileManager.saveExpenses(expenses);
    }
}