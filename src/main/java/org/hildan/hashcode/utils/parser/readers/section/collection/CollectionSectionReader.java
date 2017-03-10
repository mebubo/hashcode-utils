package org.hildan.hashcode.utils.parser.readers.section.collection;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.IntFunction;

import org.hildan.hashcode.utils.parser.Context;
import org.hildan.hashcode.utils.parser.readers.ObjectReader;
import org.hildan.hashcode.utils.parser.readers.section.SectionReader;

/**
 * A {@link SectionReader} that creates a collection, reads multiple child objects from the input, adding them to the
 * collection, and sets the created collection on the parent object.
 *
 * @param <E>
 *         the type of elements in the collection
 * @param <P>
 *         the type of parent on which this {@code ArraySectionReader} sets the created collection
 */
public class CollectionSectionReader<E, C extends Collection<E>, P> extends ContainerSectionReader<E, C, P> {

    public CollectionSectionReader(IntFunction<? extends C> constructor, ObjectReader<E> itemReader,
                                   BiFunction<P, Context, Integer> getSize, BiConsumer<P, C> parentSetter) {
        super(constructor, itemReader, getSize, parentSetter);
    }

    @Override
    protected void add(C container, int index, E element) {
        container.add(element);
    }
}