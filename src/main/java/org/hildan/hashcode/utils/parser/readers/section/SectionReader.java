package org.hildan.hashcode.utils.parser.readers.section;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.context.Context;
import org.jetbrains.annotations.NotNull;

/**
 * A reader that consumes as much input as necessary (a "section" of input) to update its parent.
 * <p>
 * For instance, this may be used to read an object that is a field of the parent class and assign the parent's field
 * with the result.
 *
 * @param <P>
 *         the type of parent that this {@code SectionReader} can update
 */
public interface SectionReader<P> {

    /**
     * Reads as much input as necessary to update a section of the given parent object.
     *
     * @param context
     *         the context to read from
     * @param parent
     *         the parent object to update
     * @throws InputParsingException
     *         if something went wrong while reading the input
     */
    void readAndSet(@NotNull Context context, @NotNull P parent) throws InputParsingException;
}
