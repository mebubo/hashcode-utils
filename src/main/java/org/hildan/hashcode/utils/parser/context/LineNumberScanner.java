package org.hildan.hashcode.utils.parser.context;

import java.io.Closeable;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Arrays;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.jetbrains.annotations.Nullable;

/**
 * A scanner that is aware of the line numbers and throws exceptions that contain this useful piece of information.
 */
public class LineNumberScanner implements Closeable, AutoCloseable {

    private final LineNumberReader reader;

    private final String delimiter;

    private String currentLineRaw;

    private String[] currentLine;

    private int nextTokenIndex;

    /**
     * Creates a new {@code LineNumberScanner} using the given reader as underlying source.
     *
     * @param reader
     *         the reader to read the data from
     * @param delimiter
     *         the delimiter to use to identify separate tokens
     */
    public LineNumberScanner(Reader reader, String delimiter) {
        this.reader = new LineNumberReader(reader);
        this.reader.setLineNumber(0);
        this.delimiter = delimiter;
    }

    /**
     * Gets the line number of the last token read. If no token has been read yet, the line number is 0. Then the
     * line numbering is 1-based.
     * <p>
     * Note that this method does not move the scanner or consume any input.
     *
     * @return the line number of the last token seen
     */
    public int getLineNumber() {
        return reader.getLineNumber();
    }

    /**
     * Gets the full line containing the last token read. If no token has been read yet, the current line is null.
     * Even if the last token read is in the middle of the line, the full line is returned from beginning to end.
     * <p>
     * Note that this method does not move the scanner or consume any input.
     *
     * @return the current line as a string
     */
    @Nullable
    public String getCurrentLine() {
        return currentLineRaw;
    }

    /**
     * Scans the next token of the input as a string.
     *
     * @return the string scanned from the input
     *
     * @throws NoMoreLinesToReadException
     *         if there is no more lines to read
     * @throws InputParsingException
     *         if an error occurs while reading the input
     */
    public String nextString() throws InputParsingException {
        while (!hasMoreTokenInCurrentLine()) {
            fetchNextLine();
        }
        return currentLine[nextTokenIndex++];
    }

    /**
     * Scans the next token of the input as an int.
     *
     * @return the int scanned from the input
     *
     * @throws NoMoreLinesToReadException
     *         if there is no more lines to read
     * @throws InputParsingException
     *         if the input could not be parsed as an int
     */
    public int nextInt() throws InputParsingException {
        String value = nextString();
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new InputParsingException(getLineNumber(), "expected int, got '" + value + "'", e);
        }
    }

    /**
     * Scans the next token of the input as a double.
     *
     * @return the double scanned from the input
     *
     * @throws NoMoreLinesToReadException
     *         if there is no more lines to read
     * @throws InputParsingException
     *         if the input could not be parsed as an int
     */
    public double nextDouble() throws InputParsingException {
        String value = nextString();
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new InputParsingException(getLineNumber(), "expected double, got '" + value + "'", e);
        }
    }

    /**
     * Reads and returns the next line of input.
     *
     * @return the next line of input
     *
     * @throws IncompleteLineReadException
     *         if the previous line was not completely consumed
     * @throws NoMoreLinesToReadException
     *         if there is no more lines to read
     * @throws InputParsingException
     *         if an error occurs while reading the input
     */
    public String nextLine() throws InputParsingException {
        fetchNextLine();
        // mark current line as consumed
        nextTokenIndex = currentLine.length;
        return currentLineRaw;
    }

    /**
     * Reads and returns the next line of input as an array of string tokens.
     *
     * @return the next line of input
     *
     * @throws IncompleteLineReadException
     *         if the previous line was not completely consumed
     * @throws NoMoreLinesToReadException
     *         if there is no more lines to read
     * @throws InputParsingException
     *         if an error occurs while reading the input
     */
    public String[] nextLineTokens() throws InputParsingException {
        fetchNextLine();
        // mark current line as consumed
        nextTokenIndex = currentLine.length;
        return currentLine;
    }

    private boolean hasMoreTokenInCurrentLine() {
        return currentLine != null && nextTokenIndex < currentLine.length;
    }

    private void fetchNextLine() throws InputParsingException {
        try {
            if (hasMoreTokenInCurrentLine()) {
                throw new IncompleteLineReadException(getLineNumber(), remainingInputOnCurrentLine());
            }
            currentLineRaw = reader.readLine();
            if (currentLineRaw == null) {
                throw new NoMoreLinesToReadException();
            }
            currentLine = currentLineRaw.isEmpty() ? new String[0] : currentLineRaw.split(delimiter, -1);
            nextTokenIndex = 0;
        } catch (IOException e) {
            throw new InputParsingException("An error occurred while reading the input", e);
        }
    }

    private String remainingInputOnCurrentLine() {
        return String.join(" ", remainingTokens());
    }

    private String[] remainingTokens() {
        return Arrays.copyOfRange(currentLine, nextTokenIndex, currentLine.length);
    }

    @Override
    public void close() {
        try {
            int nbLinesLeft = consumeAndCountRemainingLines();
            if (nbLinesLeft > 0) {
                throw new IncompleteInputReadException(nbLinesLeft);
            }
        } catch (IOException e) {
            throw new InputParsingException("An error occurred while consuming the end of the input", e);
        } finally {
            safeClose();
        }
    }

    private int consumeAndCountRemainingLines() throws IOException {
        int nbLinesLeft = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                nbLinesLeft++;
            }
        }
        return nbLinesLeft;
    }

    private void safeClose() {
        try {
            reader.close();
        } catch (IOException ignored) {
            // ignored exception
        }
    }
}
