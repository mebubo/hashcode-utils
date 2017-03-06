package org.hildan.hashcode.utils.parser.readers;

import org.hildan.hashcode.utils.parser.Context;
import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.config.Config;
import org.jetbrains.annotations.Nullable;

/**
 * Reads an object from the current {@link Context}, consuming as many lines as necessary.
 *
 * @param <T>
 *         the type of object this {@code ObjectReader} creates
 */
public interface ObjectReader<T> {

    /**
     * Reads an object from the given {@link Context}, consuming as many lines as necessary.
     *
     * @param context
     *         the context to read lines from
     * @param config
     *         the configuration defining the expected behaviour
     *
     * @return the created object, may be null
     * @throws InputParsingException
     *         if something went wrong when reading the input
     */
    @Nullable T read(Context context, Config config) throws InputParsingException;
}
