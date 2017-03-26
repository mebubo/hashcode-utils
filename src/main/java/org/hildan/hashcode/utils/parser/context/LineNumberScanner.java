package org.hildan.hashcode.utils.parser.context;

import java.io.Closeable;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Arrays;

import org.hildan.hashcode.utils.parser.InputParsingException;

public class LineNumberScanner implements Closeable, AutoCloseable {

    private final LineNumberReader reader;

    private final String delimiter;

    private String currentLineRaw;

    private String[] currentLine;

    private int nextTokenIndex;

    public LineNumberScanner(Reader reader, String delimiter) {
        this.reader = new LineNumberReader(reader);
        this.reader.setLineNumber(0);
        this.delimiter = delimiter;
    }

    public int getLineNumber() {
        return reader.getLineNumber();
    }

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
        try {
            return Integer.parseInt(nextString());
        } catch (NumberFormatException e) {
            throw new InputParsingException(getLineNumber(), "", e);
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
        }
    }
}
