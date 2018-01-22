package org.hildan.hashcode.utils.parser.readers.constructors;

import org.hildan.hashcode.utils.parser.context.Context;
import org.hildan.hashcode.utils.parser.readers.ObjectReader;
import org.jetbrains.annotations.NotNull;

/**
 * The type of a constructor with 2 int arguments.
 * <p>
 * It can also read these integers from the input in order to call {@link #create} with the parsed values, which makes
 * it an {@link ObjectReader}.
 *
 * @param <T>
 *         the type of objects that this constructor creates
 */
@FunctionalInterface
public interface Int2Constructor<T> extends ObjectReader<T> {

    /**
     * Creates a new instance.
     *
     * @param arg0
     *         an int argument
     * @param arg1
     *         an int argument
     *
     * @return the created object
     */
    T create(int arg0, int arg1);

    @Override
    default T read(@NotNull Context context) {
        return create(context.readInt(), context.readInt());
    }
}
