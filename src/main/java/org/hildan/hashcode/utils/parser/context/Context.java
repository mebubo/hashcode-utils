package org.hildan.hashcode.utils.parser.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hildan.hashcode.utils.parser.InputParsingException;

/**
 * Represents the current parsing context. It provides methods to access the input data and the context variables.
 */
public class Context {

    private final List<String> lines;

    private final Map<String, String> variables;

    private int nextLineNumber;

    /**
     * Creates a new {@link Context} with given input lines.
     *
     * @param lines
     *         the input lines to parse
     */
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

    public String getVariable(String key) throws InputParsingException {
        String value = variables.get(key);
        if (value == null) {
            throw new InputParsingException("The variable '" + key + "' has not been set in the context");
        }
        return value;
    }

    public int getVariableAsInt(String key) throws InputParsingException {
        String size = getVariable(key);
        try {
            return Integer.parseInt(size);
        } catch (NumberFormatException e) {
            throw new InputParsingException("Variable '" + key + "' cannot be converted into an int", e);
        }
    }

    public void setVariable(String key, String value) {
        variables.put(key, value);
    }

}
