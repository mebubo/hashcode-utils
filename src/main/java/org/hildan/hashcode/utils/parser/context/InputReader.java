package org.hildan.hashcode.utils.parser.context;

/**
 * Provides methods to read the input data.
 */
public interface InputReader {

    /**
     * Gets the index of the line that would be returned by a call to {@link #readLine()}.
     *
     * @return the next line's number
     */
    int getNextLineNumber();

    /**
     * Reads and returns the next line of input.
     *
     * @return the next line of input
     * @throws NoMoreLinesToReadException
     *         if there is no more lines to read
     */
    String readLine() throws NoMoreLinesToReadException;

    /**
     * Called when parsing is over, in order to release potential resources.
     *
     * @throws IncompleteReadException
     *         if there is still some input left to read
     */
    void close() throws IncompleteReadException;
}
