package org.hildan.hashcode.utils.parser.test;

import java.util.List;

public class Shape {

    public final String name;
    public final int nPoints;
    public final List<Point> points;

    public Shape(String name, Integer nPoints, List<Point> points) {
        this.name = name;
        this.nPoints = nPoints;
        this.points = points;
    }
}
