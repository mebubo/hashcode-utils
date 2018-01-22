package org.hildan.hashcode.utils.examples.basic;

import java.util.List;

import org.hildan.hashcode.utils.parser.HCParser;
import org.hildan.hashcode.utils.parser.context.Context;
import org.hildan.hashcode.utils.parser.readers.HCReader;
import org.hildan.hashcode.utils.parser.readers.ObjectReader;

public class BasicExample {

    public static void main(String[] args) {

        // manual mode using Context
        ObjectReader<Point> pointReader = (Context ctx) -> {
            double x = ctx.readDouble();
            double y = ctx.readDouble();
            return new Point(x, y);
        };

        // using the fluent API
        ObjectReader<Problem> rootReader = HCReader.withVars("N", "P")
                                                   .createFromVar(Problem::new, "P")
                                                   .thenList(Problem::setPoints, "N", pointReader);

        HCParser<Problem> parser = new HCParser<>(rootReader);
        Problem problem = parser.parse(args[0]);

        // do something with the problem
    }
}

class Point {
    private double x;

    private double y;

    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

class Problem {
    private final int someParam;

    private List<Point> points;

    Problem(int someParam) {
        this.someParam = someParam;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }
}
