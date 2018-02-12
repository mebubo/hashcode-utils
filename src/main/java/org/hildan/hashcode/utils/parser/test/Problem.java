package org.hildan.hashcode.utils.parser.test;

import java.util.List;

public class Problem {
    public final int param1;
    public final int param2;
    public final int nShapes;
    public final Shape[] shapes;

    public Problem(Integer param1, Integer param2, int nShapes, List<Shape> shapes) {
        this.param1 = param1;
        this.param2 = param2;
        this.nShapes = nShapes;
        this.shapes = shapes.toArray(new Shape[]{});
    }
}
