package org.hildan.hashcode.utils.parser.readers.line;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;

import org.hildan.hashcode.utils.parser.config.Config;
import org.hildan.hashcode.utils.parser.context.Context;

/**
 * A {@link SingleLineSectionReader} that reads the values as a single array of objects.
 *
 * @param <E>
 *         the type of elements in the created arrays
 * @param <P>
 *         the type of parent that this {@code ArrayLineReader} can update
 */
public class ArrayLineReader<E, P> extends SingleLineSectionReader<P> {

    private final BiConsumer<? super P, ? super E[]> parentSetter;

    private final Function<? super String, ? extends E> converter;

    private final IntFunction<E[]> arrayCreator;

    /**
     * Creates a new {@link ArrayLineReader}.
     *
     * @param parentSetter
     *         a setter to update the parent object using the created array
     * @param converter
     *         a function to convert each string value into an element of the array
     * @param arrayCreator
     *         a function to create an array of the right type, given the size as input
     */
    public ArrayLineReader(BiConsumer<? super P, ? super E[]> parentSetter,
                           Function<? super String, ? extends E> converter, IntFunction<E[]> arrayCreator) {
        this.parentSetter = parentSetter;
        this.converter = converter;
        this.arrayCreator = arrayCreator;
    }

    @Override
    protected void setValues(P objectToFill, String[] values, Context context, Config config) {
        E[] array = Arrays.stream(values).map(converter).toArray(arrayCreator);
        parentSetter.accept(objectToFill, array);
    }
}
