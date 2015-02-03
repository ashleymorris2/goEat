package com.uclan.ashleymorris.goeat.Classes;

/**
 * Created by Ashley Morris on 02/02/2015.
 */
public class BasketItem {

    private String itemName;
    private int itemQuantity;
    private double itemTotalCost;


    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public double getItemTotalCost() {
        return itemTotalCost;
    }

    public void setItemTotalCost(double itemTotalCost) {
        this.itemTotalCost = itemTotalCost;
    }
}
