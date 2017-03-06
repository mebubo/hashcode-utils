package org.hildan.hashcode.utils.parser.readers.line;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.hildan.hashcode.utils.parser.Context;
import org.hildan.hashcode.utils.parser.config.Config;

/**
 * A {@link SingleLineSectionReader} that reads the values as a single list of objects.
 *
 * @param <E>
 *         the type of elements in the created lists
 * @param <P>
 *         the type of parent that this {@code ArrayLineReader} can update
 */
public class ListLineReader<E, P> extends SingleLineSectionReader<P> {

    private final BiConsumer<P, ? super List<E>> parentSetter;

    private final Function<String, ? extends E> converter;

    private final Supplier<? extends List<E>> constructor;

    /**
     * Creates a new {@code ListLineReader} that reads elements as an {@link ArrayList}.
     *
     * @param converter
     *         a function to convert each string value into an element of the array
     * @param parentSetter
     *         a setter to update the parent object using the created array
     */
    public ListLineReader(Function<String, ? extends E> converter, BiConsumer<P, ? super List<E>> parentSetter) {
        this.parentSetter = parentSetter;
        this.converter = converter;
        this.constructor = ArrayList::new;
    }

    /**
     * Creates a new {@link ArrayLineReader}.
     *
     * @param converter
     *         a function to convert each string value into an element of the array
     * @param parentSetter
     *         a setter to update the parent object using the created array
     * @param constructor
     *         a function to create a list of the desired type
     */
    public ListLineReader(Function<String, ? extends E> converter, BiConsumer<P, ? super List<E>> parentSetter,
            Supplier<? extends List<E>> constructor) {
        this.parentSetter = parentSetter;
        this.converter = converter;
        this.constructor = constructor;
    }

    @Override
    protected void setValues(P objectToFill, String[] values, Context context, Config config) {
        List<E> list = Arrays.stream(values).map(converter).collect(Collectors.toCollection(constructor));
        parentSetter.accept(objectToFill, list);
    }
}
