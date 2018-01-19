package org.hildan.hashcode.utils.parser;

import java.util.List;

import org.hildan.hashcode.utils.parser.readers.RootReader;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HCParserTest {

    private static final double DELTA = 0.0001;

    public static class Problem {

        private int param1;

        private int param2;

        private int nShapes;

        public Shape[] shapes;
    }

    public static class Shape {

        public String name;

        private int nPoints;

        public List<Point> points;
    }

    public static class Point {
        private double x;

        private double y;
    }

    private static final String CONTENT = //
            "42 24 2\n" //
                    + "first 3\n" //
                    + "1.11 1.12\n" //
                    + "1.21 1.22\n" //
                    + "1.31 1.32\n" //
                    + "second 2\n" //
                    + "2.11 2.12\n" //
                    + "2.21 2.22\n";

    @Test
    public void test() {

        RootReader<Point> pointReader = RootReader.of(Point::new).fieldsAndVarsLine("x", "y");

        RootReader<Shape> shapeReader = RootReader.of(Shape::new)
                                                  .fieldsAndVarsLine("name", "nPoints")
                                                  .list((o, l) -> o.points = l, o -> o.nPoints, pointReader);

        RootReader<Problem> problemReader = RootReader.of(Problem::new)
                                                      .fieldsAndVarsLine("param1", "param2", "nShapes@N")
                                                      .array((p, l) -> p.shapes = l, Shape[]::new, "N", shapeReader);

        HCParser<Problem> hcParser = new HCParser<>(problemReader);
        Problem problem = hcParser.parse(CONTENT);

        assertEquals(42, problem.param1);
        assertEquals(24, problem.param2);
        assertEquals(2, problem.nShapes);
        assertEquals(2, problem.shapes.length);

        Shape shape0 = problem.shapes[0];
        assertEquals("first", shape0.name);
        assertEquals(3, shape0.nPoints);

        Shape shape1 = problem.shapes[1];
        assertEquals("second", shape1.name);
        assertEquals(2, shape1.nPoints);

        Point point00 = shape0.points.get(0);
        assertEquals(1.11, point00.x, DELTA);
        assertEquals(1.12, point00.y, DELTA);

        Point point01 = shape0.points.get(1);
        assertEquals(1.21, point01.x, DELTA);
        assertEquals(1.22, point01.y, DELTA);

        Point point02 = shape0.points.get(2);
        assertEquals(1.31, point02.x, DELTA);
        assertEquals(1.32, point02.y, DELTA);

        Point point10 = shape1.points.get(0);
        assertEquals(2.11, point10.x, DELTA);
        assertEquals(2.12, point10.y, DELTA);

        Point point11 = shape1.points.get(1);
        assertEquals(2.21, point11.x, DELTA);
        assertEquals(2.22, point11.y, DELTA);
    }
}
