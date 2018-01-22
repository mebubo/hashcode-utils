package org.hildan.hashcode.utils.parser.readers.variable;

import java.util.Arrays;
import java.util.function.Consumer;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.context.Context;

/**
 * A reader that consumes as much input as necessary to set the given variables.
 */
public class VariableReader implements Consumer<Context> {

    private final String[] variableNames;

    public VariableReader(String... variableNames) {
        this.variableNames = variableNames;
    }

    /**
     * Reads as much input as necessary to set the given variables.
     *
     * @param context
     *         the context to read the input from
     *
     * @throws InputParsingException
     *         if something went wrong when reading the input
     */
    @Override
    public void accept(Context context) throws InputParsingException {
        Arrays.stream(variableNames).forEach(context::readIntoVariable);
    }
}
