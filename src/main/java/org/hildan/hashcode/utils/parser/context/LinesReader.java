package org.hildan.hashcode.utils.parser.context;

import java.util.List;

/**
 * Represents the current parsing context. It provides methods to access the input data and the context variables.
 */
public class LinesReader implements InputReader {

    private final List<String> lines;

    private int nextLineNumber;

    /**
     * Creates a new {@link LinesReader} with given input lines.
     *
     * @param lines
     *         the input lines to parse
     */
    public LinesReader(List<String> lines) {
        this.lines = lines;
        this.nextLineNumber = 0;
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
