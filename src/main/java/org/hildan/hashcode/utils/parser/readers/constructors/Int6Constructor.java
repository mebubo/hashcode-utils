package org.hildan.hashcode.utils.parser.readers.constructors;

import org.hildan.hashcode.utils.parser.context.Context;
import org.hildan.hashcode.utils.parser.readers.ObjectReader;
import org.jetbrains.annotations.NotNull;

/**
 * Stands for a constructor with 6 int arguments. It can be used as an {@link ObjectReader} that reads 6 integers from
 * the input before calling {@link #create(int, int, int, int, int, int)} with the parsed values.
 *
 * @param <T>
 *         the type of objects that this constructor creates
 */
@FunctionalInterface
public interface Int6Constructor<T> extends ObjectReader<T> {

    @Override
    default T read(@NotNull Context context) {
        return create(context.readInt(), context.readInt(), context.readInt(), context.readInt(), context.readInt(),
                context.readInt());
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
     * @param arg3
     *         an int argument
     * @param arg4
     *         an int argument
     * @param arg5
     *         an int argument
     *
     * @return the created object
     */
    T create(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5);
}
