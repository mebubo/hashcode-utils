package org.hildan.hashcode.utils.examples.drones.model;

import java.util.Arrays;

public class Order {

    public int id;
    public final int row;
    public final int col;
    public final int[] quantities;

    public Order(int row, int col, int nProductTypes) {
        this.row = row;
        this.col = col;
        this.quantities = new int[nProductTypes]; // filled with 0 initially
    }

    public int getTotalItemCount() {
        return Arrays.stream(quantities).sum();
    }

    public int getId() {
        return id;
    }

    public void setItems(int[] products) {
        for (int p : products) {
            quantities[p]++;
        }
    }
}
