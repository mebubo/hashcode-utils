package org.hildan.hashcode.utils.parser.context;

import org.hildan.hashcode.utils.parser.InputParsingException;

/**
 * Thrown if the input is accessed for more lines when there is no more to read.
 */
public class NoMoreLinesToReadException extends InputParsingException {

    public NoMoreLinesToReadException() {
        super("End of input reached, cannot read more lines");
    }
}
