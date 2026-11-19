package com.chowtime.models;

import java.math.BigDecimal;

public class ManagerFoodItem {
    private final int id;
    private String itemName;
    private String description;
    private BigDecimal price;
    private int categoryID;
    private String categoryName;
    private boolean available;
    private boolean customizable;

    public ManagerFoodItem(int id, String itemName, String description, BigDecimal price, int categoryID, String categoryName, boolean available, boolean customizable) {
        this.id = id;
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.available = available;
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

    public BigDecimal getPrice() {
        return price;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isCustomizable() {
        return customizable;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setCustomizable(boolean customizable) {
        this.customizable = customizable;
    }
}
