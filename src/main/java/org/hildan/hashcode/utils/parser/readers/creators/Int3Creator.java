package org.hildan.hashcode.utils.parser.readers.creators;

import org.hildan.hashcode.utils.parser.context.Context;

/**
 * Stands for a constructor with 3 int arguments. It can be used as an {@link ObjectCreator} that reads 3 integers from
 * the input before calling {@link #create(int, int, int)} with the parsed values.
 *
 * @param <T>
 *         the type of objects that this constructor creates
 */
public interface Int3Creator<T> extends ObjectCreator<T> {

    @Override
    default T create(Context context) {
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
