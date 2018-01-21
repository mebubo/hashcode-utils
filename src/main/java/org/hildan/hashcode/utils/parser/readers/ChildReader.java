package org.hildan.hashcode.utils.parser.readers;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.context.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Reads an object from the current {@link Context}, consuming as much input as necessary. A {@code ChildReader}
 * creates an object that may require to know the parent object within which it's being constructed. This is why the
 * {@link #read(Context, Object)} method requires a non-null parent.
 *
 * @param <T>
 *         the type of object this {@code ChildReader} creates
 * @param <P>
 *         the type of parent that the read objects are part of
 */
@FunctionalInterface
public interface ChildReader<T, P> {

    /**
     * Reads an object from the given {@link Context}, consuming as much input as necessary.
     *
     * @param context
     *         the context to read lines from
     * @param parent
     *         the parent object within which the object is being created
     *
     * @return the created object, may be null
     *
     * @throws InputParsingException
     *         if something went wrong when reading the input
     */
    @Nullable
    T read(@NotNull Context context, @Nullable P parent) throws InputParsingException;
}
