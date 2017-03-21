package org.hildan.hashcode.utils.parser.context;

import java.io.LineNumberReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.config.Config;

/**
 * Represents the current parsing context. It provides methods to access the input data and the context variables.
 */
public class Context {

    private final Map<String, String> variables;

    private final LineNumberReader reader;

    private final Scanner scanner;

    /**
     * Creates a new parsing context using the given {@link Reader} to access the input.
     *
     * @param reader
     *         the reader to use to read the input
     */
    public Context(Reader reader, Config config) {
        this.variables = new HashMap<>();
        this.reader = new LineNumberReader(reader);
        this.reader.setLineNumber(1);
        this.scanner = new Scanner(this.reader).useDelimiter(config.getSeparator());
    }

    /**
     * Gets the index of the line that would be returned by a call to {@link #readLine()}.
     *
     * @return the next line's number
     */
    public int getNextLineNumber() {
        return reader.getLineNumber();
    }

    /**
     * Scans the next token of the input as an int.
     *
     * @return the int scanned from the input
     *
     * @throws NoMoreLinesToReadException
     *         if there is no more lines to read
     */
    public int readInt() {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            throw new InputParsingException(getNextLineNumber(), "", e);
        } catch (NoSuchElementException e) {
            throw new NoMoreLinesToReadException();
        }
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
        try {
            return scanner.nextLine();
        } catch (NoSuchElementException e) {
            throw new NoMoreLinesToReadException();
        }
    }

    /**
     * Releases potential resources used by the reader. Should be called when parsing is over.
     *
     * @throws IncompleteReadException
     *         if there is still some input left to read
     */
    public void closeReader() {
        try {
            int nbLinesLeft = 0;
            while (scanner.hasNextLine()) {
                if (!scanner.nextLine().trim().isEmpty()) {
                    nbLinesLeft++;
                }
            }
            if (nbLinesLeft > 0) {
                throw new IncompleteReadException(nbLinesLeft);
            }
        } finally {
            scanner.close();
        }
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
