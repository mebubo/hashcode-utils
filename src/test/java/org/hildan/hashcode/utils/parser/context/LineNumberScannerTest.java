package org.hildan.hashcode.utils.parser.context;

import java.io.StringReader;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LineNumberScannerTest {

    private static final String input = "this is a test\n42 43 -44\n\nsomething\n";

    private LineNumberScanner scanner;

    @Before
    public void setUp() {
        scanner = new LineNumberScanner(new StringReader(input), "\\s");
    }

    @Test
    public void getLineNumber() {
        assertEquals(0, scanner.getLineNumber());
        scanner.nextLine();
        assertEquals(1, scanner.getLineNumber());
        scanner.nextInt();
        assertEquals(2, scanner.getLineNumber());
        scanner.nextInt();
        scanner.nextInt();
        assertEquals(2, scanner.getLineNumber());
        scanner.nextString();
        assertEquals(4, scanner.getLineNumber());
    }

    @Test
    public void getCurrentLine() {
        assertNull(scanner.getCurrentLine());
        scanner.nextString();
        scanner.nextString();
        assertEquals("this is a test", scanner.getCurrentLine());
        scanner.nextString();
        scanner.nextString();
        assertEquals("this is a test", scanner.getCurrentLine());
        scanner.nextInt();
        scanner.nextInt();
        assertEquals("42 43 -44", scanner.getCurrentLine());
        scanner.nextInt();
        assertEquals("42 43 -44", scanner.getCurrentLine());
        scanner.nextLine();
        assertEquals("", scanner.getCurrentLine());
        scanner.nextLine();
        assertEquals("something", scanner.getCurrentLine());
    }

    @Test
    public void nextLine() {
        assertEquals("this is a test", scanner.nextLine());
        assertEquals("42 43 -44", scanner.nextLine());
        assertEquals("", scanner.nextLine());
        assertEquals("something", scanner.nextLine());
    }

    @Test(expected = NoMoreLinesToReadException.class)
    public void nextLine_failsWhenNoMoreLines() {
        scanner.nextLine();
        scanner.nextLine();
        scanner.nextLine();
        scanner.nextLine();
        scanner.nextLine();
    }

    @Test
    public void next() {
        assertEquals("this", scanner.nextString());
        assertEquals("is", scanner.nextString());
        assertEquals("a", scanner.nextString());
        assertEquals("test", scanner.nextString());
        assertEquals(42, scanner.nextInt());
        assertEquals(43, scanner.nextInt());
        assertEquals(-44, scanner.nextInt());
        assertEquals("something", scanner.nextString());
    }

    @Test(expected = InputParsingException.class)
    public void nextInt_failsOnStrings() {
        scanner.nextInt();
    }

    @Test
    public void nextLineTokens() {
        assertArrayEquals(new String[]{"this", "is", "a", "test"}, scanner.nextLineTokens());
        assertArrayEquals(new String[]{"42", "43", "-44"}, scanner.nextLineTokens());
        assertArrayEquals(new String[0], scanner.nextLineTokens());
        assertArrayEquals(new String[]{"something"}, scanner.nextLineTokens());
    }

    @Test(expected = IncompleteLineReadException.class)
    public void nextLineTokens_failsOnIncompleteLineRead() {
        assertEquals("this", scanner.nextString());
        assertEquals("is", scanner.nextString());
        scanner.nextLineTokens();
    }

    @Test(expected = IncompleteInputReadException.class)
    public void close_failsOnUnconsumedInput() {
        scanner.close();
    }
}
