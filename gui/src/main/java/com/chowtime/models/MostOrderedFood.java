package com.chowtime.models;

public class MostOrderedFood {
    private final String itemName;
    private final int totalQuantity;

    public MostOrderedFood(String itemName, int totalQuantity) {
        this.itemName = itemName;
        this.totalQuantity = totalQuantity;
    }

    public String getItemName() {
        return itemName;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }
}
