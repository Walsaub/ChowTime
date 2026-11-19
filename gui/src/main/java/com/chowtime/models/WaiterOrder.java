package com.chowtime.models;

import java.util.ArrayList;
import java.util.List;

public class WaiterOrder {
    private final int id;
    private final int tableNumber;
    private final String orderDateTime;
    private String orderStatus;
    private final List<KitchenOrderItem> items;

    public WaiterOrder(int id, int tableNumber, String orderDateTime, String orderStatus) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.orderDateTime = orderDateTime;
        this.orderStatus = orderStatus;
        this.items = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public String getOrderDateTime() {
        return orderDateTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public List<KitchenOrderItem> getItems() {
        return items;
    }

    public void addItem(KitchenOrderItem item) {
        items.add(item);
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
