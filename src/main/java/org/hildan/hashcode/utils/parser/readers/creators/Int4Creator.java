package org.hildan.hashcode.utils.parser.readers.creators;

import org.hildan.hashcode.utils.parser.context.Context;

/**
 * Stands for a constructor with 4 int arguments. It can be used as an {@link ObjectCreator} that reads 4 integers from
 * the input before calling {@link #create(int, int, int, int)} with the parsed values.
 *
 * @param <T>
 *         the type of objects that this constructor creates
 */
public interface Int4Creator<T> extends ObjectCreator<T> {

    @Override
    default T create(Context context) {
        return create(context.readInt(), context.readInt(), context.readInt(), context.readInt());
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
     *
     * @return the created object
     */
    T create(int arg0, int arg1, int arg2, int arg3);
}
