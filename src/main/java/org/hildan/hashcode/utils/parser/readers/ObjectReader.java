package org.hildan.hashcode.utils.parser.readers;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.context.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Reads an object from the current {@link Context}, consuming as much input as necessary. An {@code ObjectReader}
 * creates an object that is either the root of the object tree, or is independent from the parent object within which
 * it's being constructed.
 * <p>
 * This is anyway an extension of {@link ChildReader}, because it can be constructed in the context of a parent object,
 * but simply doesn't care about its parent.
 *
 * @param <T>
 *         the type of object this {@code ObjectReader} creates
 */
@FunctionalInterface
public interface ObjectReader<T> extends ChildReader<T, Object> {

    /**
     * Reads an object from the given {@link Context}, consuming as much input as necessary. This is a shorthand for
     * {@code read(context, null)}, to be used when the object is read as root, and no parent information is available.
     *
     * @param context
     *         the context to read lines from
     *
     * @return the created object, may be null
     * @throws InputParsingException
     *         if something went wrong when reading the input
     */
    @Nullable
    T read(@NotNull Context context) throws InputParsingException;

    @Override
    @Nullable
    default T read(@NotNull Context context, @NotNull Object parent) throws InputParsingException {
        return read(context);
    }
}
