package com.presyotrack.service;

import com.presyotrack.model.Product;
import com.presyotrack.model.PriceRecord;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

public class PriceTracker {

    private ArrayList<PriceRecord> priceRecords;
    private FileManager fileManager;

    public PriceTracker() {
        priceRecords = new ArrayList<>();
        fileManager = new FileManager();
    }

    // RECORD A NEW PRODUCT PRICE
    public void recordPrice(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }

        PriceRecord record = new PriceRecord(
                product.getName(),
                product.getPreviousPrice(),
                product.getCurrentPrice(),
                LocalDate.now()
        );

        priceRecords.add(record);
    }

    // VIEW ALL PRICE RECORDS
    public ArrayList<PriceRecord> getPriceRecords() {
        return new ArrayList<>(priceRecords);
    }

    // SEARCH PRICE HISTORY BY PRODUCT NAME
    public ArrayList<PriceRecord> getPriceHistory(String productName) {
        ArrayList<PriceRecord> history = new ArrayList<>();

        if (productName == null || productName.trim().isEmpty()) {
            return history;
        }

        for (PriceRecord record : priceRecords) {
            if (record.getProductName()
                    .equalsIgnoreCase(productName.trim())) {
                history.add(record);
            }
        }

        return history;
    }

    // CALCULATE PRICE DIFFERENCE
    public double getPriceDifference(String productName) {
        ArrayList<PriceRecord> history = getPriceHistory(productName);

        if (history.isEmpty()) {
            return 0;
        }

        PriceRecord latest = history.get(history.size() - 1);
        return latest.getNewPrice() - latest.getPreviousPrice();
    }

    // GET PRICE CHANGE STATUS
    public String getPriceStatus(String productName) {
        double difference = getPriceDifference(productName);

        if (difference > 0) {
            return "Price Increased";
        } else if (difference < 0) {
            return "Price Decreased";
        } else {
            return "No Price Change";
        }
    }

    // GET PRICE CHANGE PERCENTAGE
    public double getPriceChangePercentage(String productName) {
        ArrayList<PriceRecord> history = getPriceHistory(productName);

        if (history.isEmpty()) {
            return 0;
        }

        PriceRecord latest = history.get(history.size() - 1);

        if (latest.getPreviousPrice() == 0) {
            return 0;
        }

        return (latest.getPriceDifference()
                / latest.getPreviousPrice()) * 100;
    }

    // SORT PRICE RECORDS BY DATE
    public ArrayList<PriceRecord> sortRecordsByDate() {
        ArrayList<PriceRecord> sortedRecords = getPriceRecords();

        sortedRecords.sort(
                Comparator.comparing(PriceRecord::getDateRecorded)
        );

        return sortedRecords;
    }

    // REMOVE PRICE HISTORY OF A PRODUCT
    public boolean removePriceHistory(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            return false;
        }

        return priceRecords.removeIf(record ->
                record.getProductName()
                        .equalsIgnoreCase(productName.trim())
        );
    }

    // CLEAR ALL PRICE RECORDS
    public void clearPriceRecords() {
        priceRecords.clear();
    }

    // GENERATE PRICE SUMMARY
    public String generatePriceSummary(String productName) {
        ArrayList<PriceRecord> history = getPriceHistory(productName);

        if (history.isEmpty()) {
            return "No price history found for " + productName + ".";
        }

        PriceRecord latest = history.get(history.size() - 1);

        return String.format(
                "Product: %s%nPrevious Price: %.2f%nNew Price: %.2f%nPrice Difference: %.2f%nChange: %.2f%%%nStatus: %s",
                latest.getProductName(),
                latest.getPreviousPrice(),
                latest.getNewPrice(),
                latest.getPriceDifference(),
                getPriceChangePercentage(productName),
                getPriceStatus(productName)
        );
    }

    // LOAD SAVED PRICE HISTORY
    public void loadData() {
        priceRecords.clear();
        priceRecords.addAll(fileManager.loadPriceRecords());
    }

    // SAVE CURRENT PRICE HISTORY
    public void saveData() {
        fileManager.savePriceRecords(priceRecords);
    }
}