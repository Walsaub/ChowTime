package com.chowtime.models;

import java.math.BigDecimal;

public class MenuItem {
    private int id;
    private String itemName;
    private String description;
    private String categoryName;
    private BigDecimal price;
    private boolean customizable;

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
}
