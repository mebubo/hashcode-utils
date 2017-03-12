package org.hildan.hashcode.utils.parser.readers.line;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.hildan.hashcode.utils.parser.config.Config;
import org.hildan.hashcode.utils.parser.context.Context;

/**
 * A {@link SingleLineSectionReader} that reads the values on a line as collection of objects.
 *
 * @param <E>
 *         the type of elements in the created collection
 * @param <P>
 *         the type of parent that this {@code ArrayLineReader} can update
 */
public class CollectionLineReader<E, C extends Collection<E>, P> extends SingleLineSectionReader<P> {

    private final BiConsumer<P, ? super C> parentSetter;

    private final Function<String, ? extends E> converter;

    private final Supplier<? extends C> constructor;

    /**
     * Creates a new {@code CollectionLineReader}.
     *
     * @param converter
     *         a function to convert each string value into an element of the collection
     * @param parentSetter
     *         a setter to update the parent object using the created collection
     * @param constructor
     *         a function to create a collection of the desired type
     */
    public CollectionLineReader(Function<String, ? extends E> converter, BiConsumer<P, ? super C> parentSetter,
                                Supplier<? extends C> constructor) {
        this.parentSetter = parentSetter;
        this.converter = converter;
        this.constructor = constructor;
    }

    @Override
    protected void setValues(P objectToFill, String[] values, Context context, Config config) {
        C collection = Arrays.stream(values).map(converter).collect(Collectors.toCollection(constructor));
        parentSetter.accept(objectToFill, collection);
    }
}
