package com.chowtime.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderCartItem {
    private MenuItem menuItem;
    private int quantity;
    private List<MenuOption> selectedOptions;

    public OrderCartItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.selectedOptions = new ArrayList<>();
    }

    public OrderCartItem(MenuItem menuItem, int quantity, List<MenuOption> selectedOptions) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.selectedOptions = new ArrayList<>(selectedOptions);
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getItemName(){
        return menuItem.getItemName();
    }

    public BigDecimal getUnitPrice() {
        BigDecimal price = menuItem.getPrice();
        for (MenuOption option : selectedOptions){
            price = price.add(option.getPrice());
        }
        return price;
    }

    public BigDecimal getSubtotal(){
        return getUnitPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public List<MenuOption> getSelectedOptions(){
        return selectedOptions;
    }

    public String getOptionsDescription(){
        if (selectedOptions.isEmpty()){
            return "";
        }

        StringBuilder description = new StringBuilder();

        for (MenuOption option : selectedOptions){
            if (!description.isEmpty()){
                description.append(", ");
            }
            description.append(option.getOptionName());
        }
        return description.toString();
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }
}
