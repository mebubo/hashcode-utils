package org.hildan.hashcode.utils.parser.readers.builder;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.context.Context;

/**
 * A reader meant to update any sort of state (in particular context variables) by consuming as much input as necessary.
 */
@FunctionalInterface
public interface StateReader {

    /**
     * Reads as much input as necessary and updates some state accordingly.
     *
     * @param context
     *         the context to read the input from
     *
     * @throws InputParsingException
     *         if something went wrong when reading the input
     */
    void read(Context context) throws InputParsingException;
}
