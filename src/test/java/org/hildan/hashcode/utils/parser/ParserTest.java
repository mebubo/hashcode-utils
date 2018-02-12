package org.hildan.hashcode.utils.parser;

import org.hildan.hashcode.utils.parser.test.Point;
import org.hildan.hashcode.utils.parser.test.Problem;
import org.hildan.hashcode.utils.parser.test.Shape;
import org.junit.Test;

import org.hildan.hashcode.utils.parser.test.ProblemParsers;
import static org.junit.Assert.assertEquals;

public class ParserTest {

    private static final double DELTA = 0.0001;

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

        Parser<Problem> problemParser = ProblemParsers.problem();

        HCParser<Problem> hcParser = new HCParser<>(problemParser);
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
