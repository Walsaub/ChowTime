package com.chowtime.models;

public class WaiterTable {
    private final int id;
    private final int tableNumber;
    private final int capacity;
    private final String status;

    public WaiterTable(int id, int tableNumber, int capacity, String status) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getStatus() {
        return status;
    }
}
