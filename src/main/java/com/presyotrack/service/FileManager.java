
package com.presyotrack.service;

import com.presyotrack.model.*;

import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

public class FileManager {

    private static final String PRODUCTS_FILE = "products.txt";
    private static final String EXPENSES_FILE = "expenses.txt";
    private static final String BUDGET_FILE = "budget.txt";
    private static final String PRICE_RECORDS_FILE = "price_records.txt";

    // =========================
    // SAVE PRODUCTS
    // =========================

    public void saveProducts(ArrayList<Product> products) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(PRODUCTS_FILE))) {

            for (Product product : products) {
                writer.write(product.getProductType() + "|"
                        + clean(product.getName()) + "|"
                        + clean(product.getCurrentPrice()) + "|"
                        + clean(product.getPreviousPrice()));
                writer.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error saving products: " + e.getMessage());
        }
    }

    // LOAD PRODUCTS

    public ArrayList<Product> loadProducts() {
        ArrayList<Product> products = new ArrayList<>();

        File file = new File(PRODUCTS_FILE);
        if (!file.exists()) {
            return products;
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    String[] data = line.split("\\|", -1);

                    if (data.length != 4) continue;

                    String type = data[0];
                    String name = data[1];
                    double current = Double.parseDouble(data[2]);
                    double previous = Double.parseDouble(data[3]);

                    if (type.equals("Food Product")) {
                        products.add(new FoodProduct(name, current, previous));
                    } else if (type.equals("Household Product")) {
                        products.add(new HouseholdProduct(name, current, previous));
                    }

                } catch (IllegalArgumentException e) {
                    System.out.println("Skipping invalid product record.");
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading products: " + e.getMessage());
        }

        return products;
    }

    // =========================
    // SAVE EXPENSES
    // =========================

    public void saveExpenses(ArrayList<Expense> expenses) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(EXPENSES_FILE))) {

            for (Expense expense : expenses) {
                writer.write(clean(expense.getName()) + "|"
                        + clean(expense.getCategory()) + "|"
                        + expense.getAmount() + "|"
                        + expense.getDate() + "|"
                        + clean(expense.getPayment()));
                writer.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error saving expenses: " + e.getMessage());
        }
    }

    // LOAD EXPENSES

    public ArrayList<Expense> loadExpenses() {
        ArrayList<Expense> expenses = new ArrayList<>();

        File file = new File(EXPENSES_FILE);
        if (!file.exists()) {
            return expenses;
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    String[] data = line.split("\\|", -1);

                    if (data.length != 5) continue;

                    Expense expense = new Expense(
                            data[0],
                            data[1],
                            Double.parseDouble(data[2]),
                            LocalDate.parse(data[3]),
                            data[4]
                    );

                    expenses.add(expense);

                } catch (RuntimeException e) {
                    System.out.println("Skipping invalid expense record.");
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading expenses: " + e.getMessage());
        }

        return expenses;
    }

    // =========================
    // SAVE BUDGET
    // =========================

    public void saveBudget(Budget budget) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(BUDGET_FILE))) {

            writer.write(budget.getAmount() + "|"
                    + budget.getMonth());

        } catch (IOException e) {
            System.out.println("Error saving budget: " + e.getMessage());
        }
    }

    // LOAD BUDGET

    public Budget loadBudget() {
        File file = new File(BUDGET_FILE);

        if (!file.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {

            String line = reader.readLine();

            if (line != null) {
                String[] data = line.split("\\|", -1);

                if (data.length == 2) {
                    return new Budget(
                            Double.parseDouble(data[0]),
                            YearMonth.parse(data[1])
                    );
                }
            }

        } catch (IOException | RuntimeException e) {
            System.out.println("Error loading budget: " + e.getMessage());
        }

        return null;
    }

    // =========================
    // SAVE PRICE RECORDS
    // =========================

    public void savePriceRecords(ArrayList<PriceRecord> records) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(PRICE_RECORDS_FILE))) {

            for (PriceRecord record : records) {
                writer.write(clean(record.getProductName()) + "|"
                        + record.getPreviousPrice() + "|"
                        + record.getNewPrice() + "|"
                        + record.getDateRecorded());
                writer.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error saving price records: " + e.getMessage());
        }
    }

    // LOAD PRICE RECORDS

    public ArrayList<PriceRecord> loadPriceRecords() {
        ArrayList<PriceRecord> records = new ArrayList<>();

        File file = new File(PRICE_RECORDS_FILE);
        if (!file.exists()) {
            return records;
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    String[] data = line.split("\\|", -1);

                    if (data.length != 4) continue;

                    records.add(new PriceRecord(
                            data[0],
                            Double.parseDouble(data[1]),
                            Double.parseDouble(data[2]),
                            LocalDate.parse(data[3])
                    ));

                } catch (RuntimeException e) {
                    System.out.println("Skipping invalid price record.");
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading price records: " + e.getMessage());
        }

        return records;
    }

    // Remove delimiter characters from text fields
    private String clean(String value) {
        return value.replace("|", "/")
                    .replace("\n", " ")
                    .replace("\r", " ");
    }

    private String clean(double value) {
        return String.valueOf(value);
    }
}
