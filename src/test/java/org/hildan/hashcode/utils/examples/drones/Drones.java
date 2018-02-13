package org.hildan.hashcode.utils.examples.drones;

import org.hildan.hashcode.utils.examples.drones.model.Order;
import org.hildan.hashcode.utils.examples.drones.model.Simulation;
import org.hildan.hashcode.utils.examples.drones.model.Warehouse;
import org.hildan.hashcode.utils.parser.HCParser;
import org.hildan.hashcode.utils.parser.Parser;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class Drones {

    private static final String input =
            "100 100 3 50 500\n"    // 100 rows, 100 columns, 3 drones, 50 turns, max payload is 500u
                    + "3\n"         // There are 3 different product types
                    + "100 5 450\n" // The product types weigh: 100u, 5u, 450u
                    + "2\n"      // There are 2 warehouses
                    + "0 0\n"    // First warehouse is located at [0, 0]
                    + "5 1 0\n"  // It stores 5 items of product 0 and 1 of product 1
                    + "5 5\n"    // Second warehouse is located at [5, 5]
                    + "0 10 2\n" // It stores 10 items of product 1 and 2 items of product 2
                    + "3\n"      // There are 3 orders
                    + "1 1\n"    // First order to be delivered to [1, 1]
                    + "2\n"      // First order contains 2 items
                    + "2 0\n"    // Items of product types: 2, 0
                    + "3 3\n"    // Second order to be delivered to [3, 3]
                    + "1\n"      // Second order contains 1 item
                    + "0\n"      // Items of product types: 0
                    + "5 6\n"    // Third order to be delivered to [5, 6]
                    + "1\n"      // Third order contains 1 item
                    + "2\n";     // Items of product types: 2


    @Test
    public void test_parser() {
        Parser<Simulation> rootReader = DroneParsers.simulation();
        HCParser<Simulation> parser = new HCParser<>(rootReader);
        Simulation problem = parser.parse(input);

        assertEquals(100, problem.nRows);
        assertEquals(100, problem.nCols);
        assertEquals(3, problem.nDrones);
        assertEquals(50, problem.nTurns);
        assertEquals(500, problem.maxLoad);

        assertEquals(3, problem.nProductTypes);
        assertEquals(3, problem.productTypeWeights.length);
        assertArrayEquals(new int[] {100, 5, 450}, problem.productTypeWeights);

        assertEquals(2, problem.warehouses.length);

        Warehouse w0 = problem.warehouses[0];
        assertEquals(0, w0.row);
        assertEquals(0, w0.col);
        assertArrayEquals(new int[] {5, 1, 0}, w0.stocks);

        Warehouse w1 = problem.warehouses[1];
        assertEquals(5, w1.row);
        assertEquals(5, w1.col);
        assertArrayEquals(new int[] {0, 10, 2}, w1.stocks);

        assertEquals(3, problem.orders.length);

        Order order0 = problem.orders[0];
        assertEquals(1, order0.row);
        assertEquals(1, order0.col);
        assertArrayEquals(new int[] {1, 0, 1}, order0.quantities);

        Order order1 = problem.orders[1];
        assertEquals(3, order1.row);
        assertEquals(3, order1.col);
        assertArrayEquals(new int[] {1, 0, 0}, order1.quantities);

        Order order2 = problem.orders[2];
        assertEquals(5, order2.row);
        assertEquals(6, order2.col);
        assertArrayEquals(new int[] {0, 0, 1}, order2.quantities);
    }
}
