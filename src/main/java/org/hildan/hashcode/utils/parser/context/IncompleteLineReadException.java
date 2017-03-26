package org.hildan.hashcode.utils.parser.context;

import org.hildan.hashcode.utils.parser.InputParsingException;

/**
 * Thrown if the next line is accessed while the current line has not been fully consumed.
 */
public class IncompleteLineReadException extends InputParsingException {

    public IncompleteLineReadException(int lineNum, String content) {
        super(String.format("The end of line %d was not consumed, remaining tokens: %s", lineNum, content));
    }
}
