package com.chowtime.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MenuItem {
    private int id;
    private String itemName;
    private String description;
    private String categoryName;
    private BigDecimal price;
    private boolean customizable;
    private List<String> allergens = new ArrayList<>();

    public MenuItem(int id, String itemName, String description, String categoryName, BigDecimal price, boolean customizable) {
        this.id = id;
        this.itemName = itemName;
        this.description = description;
        this.categoryName = categoryName;
        this.price = price;
        this.customizable = customizable;
    }

    public int getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isCustomizable() {
        return customizable;
    }

    public List<String> getAllergens() {
        return allergens;
    }

    public void setAllergens(List<String> allergens) {
        this.allergens = allergens;
    }

    public String getAllergensDescription() {
        if (allergens == null || allergens.isEmpty()) {
            return "None";
        }

        return String.join(", ", allergens);
    }
}
