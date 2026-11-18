package com.chowtime.models;

import java.math.BigDecimal;

public class MenuOption {
    private final int id;
    private final String optionName;
    private final BigDecimal price;

    public MenuOption(int id, String optionName, BigDecimal price) {
        this.id = id;
        this.optionName = optionName;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getOptionName() {
        return optionName;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
