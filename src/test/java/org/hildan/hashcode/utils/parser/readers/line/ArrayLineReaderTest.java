package org.hildan.hashcode.utils.parser.readers.line;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hildan.hashcode.utils.parser.Context;
import org.hildan.hashcode.utils.parser.config.Config;
import org.junit.Test;

import static org.junit.Assert.*;

public class ArrayLineReaderTest {

    // TODO use theories to test multiple use cases (empty array, singleton array etc)

    @Test
    public void test() {
        ArrayLineReader<Integer, List<Integer[]>> reader = new ArrayLineReader<>(Integer[]::new, Integer::parseInt,
                List::add);

        List<Integer[]> parentMock = new ArrayList<>(1);
        Context context = new Context(Collections.singletonList("42 10 23"));

        reader.readSection(parentMock, context, new Config());

        assertEquals(1, parentMock.size());

        Integer[] expectedArray = new Integer[] {42, 10, 23};
        assertArrayEquals(expectedArray, parentMock.get(0));
    }
}