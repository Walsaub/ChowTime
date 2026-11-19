package com.chowtime.models;

public class WaiterHelpRequest {
    private final int id;
    private final int tableNumber;
    private final String requestTime;
    private String requestStatus;

    public WaiterHelpRequest(int id, int tableNumber, String requestTime, String requestStatus) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.requestTime = requestTime;
        this.requestStatus = requestStatus;
    }

    public int getId() {
        return id;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus){
        this.requestStatus = requestStatus;
    }
}
