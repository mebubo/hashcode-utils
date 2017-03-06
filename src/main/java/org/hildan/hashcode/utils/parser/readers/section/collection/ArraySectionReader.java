package org.hildan.hashcode.utils.parser.readers.section.collection;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.IntFunction;

import org.hildan.hashcode.utils.parser.Context;
import org.hildan.hashcode.utils.parser.readers.ObjectReader;
import org.hildan.hashcode.utils.parser.readers.section.SectionReader;

/**
 * A {@link SectionReader} that creates an array, reads multiple child objects from the input, adding them to the array,
 * and sets the created array on the parent object.
 *
 * @param <E>
 *         the type of elements in the array
 * @param <P>
 *         the type of parent on which this {@code ArraySectionReader} sets the created array
 */
public class ArraySectionReader<E, P> extends ContainerSectionReader<E, E[], P> {

    public ArraySectionReader(IntFunction<? extends E[]> arrayCreator, ObjectReader<E> itemReader,
                              BiFunction<P, Context, Integer> getSize, BiConsumer<P, ? super E[]> parentSetter) {
        super(arrayCreator, itemReader, getSize, parentSetter);
    }

    @Override
    protected void add(E[] container, int index, E element) {
        container[index] = element;
    }
}
