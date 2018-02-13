package org.hildan.hashcode.utils.examples.drones.model;

import java.util.List;

public class Warehouse {

    public final int row;
    public final int col;
    public int[] stocks;

    public Warehouse(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void setStocks(List<Integer> stocks) {
        this.stocks = new int[stocks.size()];
        for (int i = 0; i < stocks.size(); i++) {
            this.stocks[i] = stocks.get(i);
        }
    }
}
