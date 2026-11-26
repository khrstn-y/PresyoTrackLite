package com.presyotrack;

import com.presyotrack.model.Budget;
import com.presyotrack.model.Expense;
import com.presyotrack.service.BudgetManager;
import com.presyotrack.service.BudgetReport;

import java.time.LocalDate;
import java.time.YearMonth;

public class BudgetReportTest {

    public static void main(String[] args) {

        try {
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

            manager.addExpense(new Expense(
                    "Chicken", "Food", 800,
                    LocalDate.now(), "Cash"
            ));

            BudgetReport report = new BudgetReport(manager);

            if (report.getTotalExpenses() != 3500) {
                throw new AssertionError("Total expenses test failed.");
            }

            if (report.getRemainingBudget() != 6500) {
                throw new AssertionError("Remaining budget test failed.");
            }

            if (report.getBudgetUtilization() != 35.0) {
                throw new AssertionError("Budget utilization test failed.");
            }

            if (!report.getBudgetStatus().equals("Within Budget")) {
                throw new AssertionError("Budget status test failed.");
            }

            if (report.getExpensesByCategory().get("Food") != 2300) {
                throw new AssertionError("Category grouping test failed.");
            }

            if (!report.generateReport().contains(
                    "PRESYOTRACK BUDGET REPORT")) {
                throw new AssertionError("Report generation test failed.");
            }

            System.out.println("ALL BUDGET REPORT TESTS PASSED!");
            System.out.println("\n" + report.generateReport());

        } catch (Exception e) {
            System.out.println("TEST FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}