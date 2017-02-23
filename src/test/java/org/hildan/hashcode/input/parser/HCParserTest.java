package org.hildan.hashcode.input.parser;

import java.util.Arrays;
import java.util.List;

import org.hildan.hashcode.input.parser.readers.ObjectReader;
import org.hildan.hashcode.input.parser.readers.TreeObjectReader;
import org.junit.Test;

import static org.junit.Assert.*;

public class HCParserTest {

    private static final double DELTA = 0.0001;

    public static class Global {
        private int param1;

        private int param2;

        private int nOuter;

        public Outer[] myArray;
    }

    public static class Outer {

        public String name;

        private int nInner;

        public List<Inner> mySubList;
    }

    public static class Inner {
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

        TreeObjectReader<Inner> innerReader = TreeObjectReader.of(Inner::new).addFieldsLine("x", "y");

        TreeObjectReader<Outer> outerReader = TreeObjectReader.of(Outer::new)
                .addFieldsLine("name", "nInner")
                .addList((o, l) -> o.mySubList = l, o -> o.nInner, innerReader);

        ObjectReader<Global> globalReader = TreeObjectReader.of(Global::new)
                .addFieldsLine("param1", "param2", "nOuter@myArraySize")
                .addArray((g, l) -> g.myArray = l, Outer[]::new, "myArraySize", outerReader);

        List<String> lines = Arrays.asList(CONTENT.split("\\n"));
        HCParser<Global> hcParser = new HCParser<>(globalReader);
        Global global = hcParser.parse(lines);

        assertEquals(42, global.param1);
        assertEquals(24, global.param2);
        assertEquals(2, global.nOuter);
        assertEquals(2, global.myArray.length);

        Outer outer0 = global.myArray[0];
        assertEquals("first", outer0.name);
        assertEquals(3, outer0.nInner);

        Outer outer1 = global.myArray[1];
        assertEquals("second", outer1.name);
        assertEquals(2, outer1.nInner);

        Inner inner00 = outer0.mySubList.get(0);
        assertEquals(1.11, inner00.x, DELTA);
        assertEquals(1.12, inner00.y, DELTA);

        Inner inner01 = outer0.mySubList.get(1);
        assertEquals(1.21, inner01.x, DELTA);
        assertEquals(1.22, inner01.y, DELTA);

        Inner inner02 = outer0.mySubList.get(2);
        assertEquals(1.31, inner02.x, DELTA);
        assertEquals(1.32, inner02.y, DELTA);

        Inner inner10 = outer1.mySubList.get(0);
        assertEquals(2.11, inner10.x, DELTA);
        assertEquals(2.12, inner10.y, DELTA);

        Inner inner11 = outer1.mySubList.get(1);
        assertEquals(2.21, inner11.x, DELTA);
        assertEquals(2.22, inner11.y, DELTA);
    }
}