package org.hildan.hashcode.input.parser;

import java.util.List;

import org.hildan.hashcode.input.InputParsingException;

public class Context {

    private final List<String> lines;

    private int nextLineNumber;

    public Context(List<String> lines) {
        this.lines = lines;
        this.nextLineNumber = 0;
    }

    public int getNextLineNumber() {
        return nextLineNumber;
    }

    public String readLine() {
        if (nextLineNumber >= lines.size()) {
            throw new NoMoreLinesToReadException();
        }
        return lines.get(nextLineNumber++);
    }

    private static class NoMoreLinesToReadException extends InputParsingException {}
}
