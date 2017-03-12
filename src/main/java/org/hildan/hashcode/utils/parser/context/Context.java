package org.hildan.hashcode.utils.parser.context;

import java.util.HashMap;
import java.util.Map;

import org.hildan.hashcode.utils.parser.InputParsingException;

/**
 * Represents the current parsing context. It provides methods to access the input data and the context variables.
 */
public class Context {

    private final Map<String, String> variables;

    private final InputReader reader;

    public Context(InputReader reader) {
        this.variables = new HashMap<>();
        this.reader = reader;
    }

    public int getNextLineNumber() {
        return reader.getNextLineNumber();
    }

    public String readLine() {
        return reader.readLine();
    }

    public void closeReader() {
        reader.close();
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
