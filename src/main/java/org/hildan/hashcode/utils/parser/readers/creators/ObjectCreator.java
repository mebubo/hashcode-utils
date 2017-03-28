package org.hildan.hashcode.utils.parser.readers.creators;

import org.hildan.hashcode.utils.parser.context.Context;

/**
 * Creates instances based on the current parsing {@link Context}. Used mostly to create objects based on context
 * variables that were set up front by reading a few lines of input.
 *
 * @param <T>
 *         the type of objects that this constructor creates
 */
@FunctionalInterface
public interface ObjectCreator<T> {

    /**
     * Creates a new instance based on the given {@link Context}.
     *
     * @param context
     *         the parsing context to read variables or input from
     *
     * @return the created object
     */
    T create(Context context);
}
