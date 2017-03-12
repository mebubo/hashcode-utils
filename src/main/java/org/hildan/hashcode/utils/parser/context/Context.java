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

    /**
     * Creates a new parsing context using the given {@link InputReader} to access the input.
     *
     * @param reader
     *         the reader to use to read the input
     */
    public Context(InputReader reader) {
        this.variables = new HashMap<>();
        this.reader = reader;
    }

    /**
     * Gets the index of the line that would be returned by a call to {@link #readLine()}.
     *
     * @return the next line's number
     */
    public int getNextLineNumber() {
        return reader.getNextLineNumber();
    }

    /**
     * Reads and returns the next line of input.
     *
     * @return the next line of input
     *
     * @throws NoMoreLinesToReadException
     *         if there is no more lines to read
     */
    public String readLine() {
        return reader.readLine();
    }

    /**
     * Releases potential resources used by the reader. Should be called when parsing is over.
     *
     * @throws IncompleteReadException
     *         if there is still some input left to read
     */
    public void closeReader() {
        reader.close();
    }

    /**
     * Gets the value of the given context variable.
     *
     * @param key
     *         the name of the context variable to access
     *
     * @return the value of the given context variable
     *
     * @throws UndefinedVariableException
     *         if no variable is found with the given name
     */
    public String getVariable(String key) throws UndefinedVariableException {
        String value = variables.get(key);
        if (value == null) {
            throw new UndefinedVariableException(key);
        }
        return value;
    }

    /**
     * Gets the value of the given context variable, converted into an int.
     *
     * @param key
     *         the name of the context variable to access
     *
     * @return the value of the given context variable
     *
     * @throws UndefinedVariableException
     *         if no variable is found with the given name
     * @throws InputParsingException
     *         if the variable value cannot be converted to an int
     */
    public int getVariableAsInt(String key) throws InputParsingException {
        String size = getVariable(key);
        try {
            return Integer.parseInt(size);
        } catch (NumberFormatException e) {
            throw new InputParsingException("Variable '" + key + "' cannot be converted into an int", e);
        }
    }

    /**
     * Sets the given variable to the given value.
     *
     * @param key
     *         the name of the context variable to create or update
     * @param value
     *         the value to set the variable to
     */
    public void setVariable(String key, String value) {
        variables.put(key, value);
    }
}
