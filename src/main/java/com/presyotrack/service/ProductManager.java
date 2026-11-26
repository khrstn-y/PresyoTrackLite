package com.presyotrack.service;

import com.presyotrack.model.Product;
import com.presyotrack.model.FoodProduct;
import com.presyotrack.model.HouseholdProduct;
import com.presyotrack.interfaces.Reportable;
import com.presyotrack.exceptions.InvalidPriceException;

import java.util.ArrayList;
import java.util.Comparator;

public class ProductManager {

    private ArrayList<Product> products;
    private FileManager fileManager;

    public ProductManager() {
        products = new ArrayList<>();
        fileManager = new FileManager();
    }

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be empty.");
        }
        products.add(product);
    }

    public ArrayList<Product> getProducts() {
        return new ArrayList<>(products);
    }

    public Product searchProduct(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        for (Product product : products) {
            if (product.getName().equalsIgnoreCase(name.trim())) {
                return product;
            }
        }
        return null;
    }

    public boolean removeProduct(String name) {
        Product product = searchProduct(name);

        if (product == null) {
            return false;
        }

        products.remove(product);
        return true;
    }

    public boolean updateProductPrice(String name, double newPrice)
            throws InvalidPriceException {

        Product product = searchProduct(name);

        if (product == null) {
            return false;
        }

        product.updateCurrentPrice(newPrice);
        return true;
    }

    public ArrayList<Product> getProductsByCategory(String category) {
        ArrayList<Product> filteredProducts = new ArrayList<>();

        if (category == null || category.trim().isEmpty()) {
            return filteredProducts;
        }

        for (Product product : products) {
            if (product.getCategory().equalsIgnoreCase(category.trim())) {
                filteredProducts.add(product);
            }
        }

        return filteredProducts;
    }

    public ArrayList<Product> sortProductsByPrice() {
        ArrayList<Product> sortedProducts = getProducts();

        sortedProducts.sort(
                Comparator.comparingDouble(Product::getCurrentPrice)
        );

        return sortedProducts;
    }

    public ArrayList<String> generateProductReports() {
        ArrayList<String> reports = new ArrayList<>();

        for (Product product : products) {
            if (product instanceof Reportable) {
                Reportable reportable = (Reportable) product;
                reports.add(reportable.generateReport());
            }
        }

        return reports;
    }

    public int countFoodProducts() {
        int count = 0;

        for (Product product : products) {
            if (product instanceof FoodProduct) {
                count++;
            }
        }

        return count;
    }

    public int countHouseholdProducts() {
        int count = 0;

        for (Product product : products) {
            if (product instanceof HouseholdProduct) {
                count++;
            }
        }

        return count;
    }

    public void clearProducts() {
        products.clear();
    }

    public void loadData() {
        products.clear();
        products.addAll(fileManager.loadProducts());
    }

    public void saveData() {
        fileManager.saveProducts(products);
    }
}