package org.hildan.hashcode.utils.parser.readers.line;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.config.Config;
import org.hildan.hashcode.utils.parser.context.Context;
import org.hildan.hashcode.utils.parser.readers.section.SectionReader;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link SectionReader} that consumes a single line of input, and updates its parent accordingly. It may read a
 * single or multiple objects form that single line, depending on the implementations.
 *
 * @param <P>
 *         the type of parent that this {@code SingleLineSectionReader} can update
 */
public abstract class SingleLineSectionReader<P> implements SectionReader<P> {

    @Override
    public void readSection(@NotNull P parent, @NotNull Context context) throws
            InputParsingException {
        int lineNum = context.getNextLineNumber();
        String[] values = context.readArrayLine();
        try {
            setValues(parent, values, context, null);
        } catch (Exception e) {
            throw new InputParsingException(lineNum, String.join(" ", values), e.getMessage(), e);
        }
    }

    /**
     * Updates the parent object using the read values.
     *
     * @param objectToFill
     *         the parent object to update
     * @param values
     *         the values that were on the line that was just read
     * @param context
     *         the current context, which may be of use to get variables
     * @param config
     *         the configuration defining the expected behaviour
     *
     * @throws Exception
     *         if anything goes wrong. The thrown exception will be wrapped into an {@link InputParsingException}
     *         containing useful contextual information such as line number and line content
     */
    protected abstract void setValues(P objectToFill, String[] values, Context context, Config config) throws Exception;
}
