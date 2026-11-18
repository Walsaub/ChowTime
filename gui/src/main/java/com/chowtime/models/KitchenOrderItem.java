package com.chowtime.models;

public class KitchenOrderItem {
    private final String itemName;
    private final int quantity;
    private final String options;

    public KitchenOrderItem(String itemName, int quantity, String options) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.options = options == null || options.isBlank() ? "None" : options;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getOptions() {
        return options;
    }
}
