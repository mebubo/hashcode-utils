package org.hildan.hashcode.utils.examples.drones.model;

public class Simulation {

    public final int nRows;

    public final int nCols;

    public final int nDrones;

    public final int nTurns;

    public final int nProductTypes;

    public final int maxLoad;

    public int[] productTypeWeights;

    public Warehouse[] warehouses;

    public Order[] orders;

    public Simulation(int nRows, int nCols, int nDrones, int nTurns, int maxLoad, int nProductTypes) {
        this.nRows = nRows;
        this.nCols = nCols;
        this.nDrones = nDrones;
        this.nTurns = nTurns;
        this.nProductTypes = nProductTypes;
        this.maxLoad = maxLoad;
    }

    public void setOrders(Order[] orders) {
        for (int i = 0; i < orders.length; i++) {
            orders[i].id = i;
        }
        this.orders = orders;
    }
}
