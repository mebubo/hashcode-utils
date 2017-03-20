package org.hildan.hashcode.utils.parser.context;

import java.util.List;

/**
 * An input reader based on a list of lines to read.
 */
public class LinesReader implements InputReader {

    private final List<String> lines;

    private int nextLineNumber;

    /**
     * Creates a new {@link LinesReader} with the given input lines.
     *
     * @param lines
     *         the input lines to parse
     */
    public LinesReader(List<String> lines) {
        this.lines = lines;
        this.nextLineNumber = 1;
    }

    @Override
    public int getNextLineNumber() {
        return nextLineNumber;
    }

    @Override
    public String readLine() {
        if (nextLineNumber >= lines.size()) {
            throw new NoMoreLinesToReadException();
        }
        return lines.get(nextLineNumber++);
    }

    @Override
    public void close() {
        int nbLinesLeft = lines.size() - nextLineNumber;
        if (nbLinesLeft > 0) {
            throw new IncompleteReadException(nbLinesLeft);
        }
    }
}
