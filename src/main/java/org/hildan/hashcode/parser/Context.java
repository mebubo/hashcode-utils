package org.hildan.hashcode.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Context {

    private final List<String> lines;

    private final Map<String, String> variables;

    private int nextLineNumber;

    public Context(List<String> lines) {
        this.lines = lines;
        this.nextLineNumber = 0;
        this.variables = new HashMap<>();
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

    public String getVariable(String key) {
        String value = variables.get(key);
        if (value == null) {
            throw new InputParsingException("The variable '" + key + "' has not been set in the context");
        }
        return value;
    }

    public void setVariable(String key, String value) {
        variables.put(key, value);
    }

    private static class NoMoreLinesToReadException extends InputParsingException {}
}
