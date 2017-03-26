package org.hildan.hashcode.utils.parser.readers;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.context.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Reads an object from the current {@link Context}, consuming as many lines as necessary.
 *
 * @param <T>
 *         the type of object this {@code ObjectReader} creates
 * @param <P>
 *         the type of parent that the read objects are part of (irrelevant if the objects are independent, should be
 *         {@code Object} in this case)
 */
public interface ObjectReader<T, P> {

    /**
     * Reads an object from the given {@link Context}, consuming as many lines as necessary.
     *
     * @param context
     *         the context to read lines from
     * @param parent
     *         the parent object within which the object is being created, or null of the object is independent
     *
     * @return the created object, may be null
     *
     * @throws InputParsingException
     *         if something went wrong when reading the input
     */
    @Nullable
    T read(@NotNull Context context, @Nullable P parent) throws InputParsingException;

    /**
     * Reads an object from the given {@link Context}, consuming as many lines as necessary.
     *
     * @param context
     *         the context to read lines from
     *
     * @return the created object, may be null
     *
     * @throws InputParsingException
     *         if something went wrong when reading the input
     */
    @Nullable
    default T read(@NotNull Context context) throws InputParsingException {
        return read(context, null);
    }
}
