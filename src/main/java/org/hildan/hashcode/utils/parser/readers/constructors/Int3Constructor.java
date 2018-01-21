package org.hildan.hashcode.utils.parser.readers.constructors;

import org.hildan.hashcode.utils.parser.context.Context;
import org.hildan.hashcode.utils.parser.readers.ObjectReader;
import org.jetbrains.annotations.NotNull;

/**
 * Stands for a constructor with 3 int arguments. It can also read these integers from the input in order to call
 * {@link #create(int, int, int)} with the parsed values, which makes it an {@link ObjectReader}.
 *
 * @param <T>
 *         the type of objects that this constructor creates
 */
public interface Int3Constructor<T> extends ObjectReader<T> {

    @Override
    default T read(@NotNull Context context) {
        return create(context.readInt(), context.readInt(), context.readInt());
    }

    /**
     * Creates a new instance.
     *
     * @param arg0
     *         an int argument
     * @param arg1
     *         an int argument
     * @param arg2
     *         an int argument
     *
     * @return the created object
     */
    T create(int arg0, int arg1, int arg2);
}
