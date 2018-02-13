package org.hildan.hashcode.utils.examples.drones.model;

import java.util.List;

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

    public void setOrders(List<Order> orders) {
        this.orders = new Order[orders.size()];
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            order.id = i;
            this.orders[i] = order;
        }
    }

    public void setWarehouses(List<Warehouse> warehouses) {
        this.warehouses = new Warehouse[warehouses.size()];
        for (int i = 0; i < warehouses.size(); i++) {
            this.warehouses[i] = warehouses.get(i);
        }
    }

    public void setnProductTypeWeights(List<Integer> weights) {
        this.productTypeWeights = new int[weights.size()];
        for (int i = 0; i < weights.size(); i++) {
            this.productTypeWeights[i] = weights.get(i);
        }
    }
}
