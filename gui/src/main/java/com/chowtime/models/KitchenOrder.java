package com.chowtime.models;

import java.util.ArrayList;
import java.util.List;

public class KitchenOrder {
    private final int id;
    private final int tableNumber;
    private final String waiterName;
    private final String orderDateTime;
    private String orderStatus;
    private final List<KitchenOrderItem> items;

    public KitchenOrder(int id, int tableNumber, String waiterName, String orderDateTime, String orderStatus) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.waiterName = waiterName;
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

    public String getWaiterName() {
        return waiterName;
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
