package org.hildan.hashcode.utils.parser.context;

import org.hildan.hashcode.utils.parser.InputParsingException;

public class NoMoreLinesToReadException extends InputParsingException {

    public NoMoreLinesToReadException() {
        super("End of input reached, cannot read more lines");
    }
}
