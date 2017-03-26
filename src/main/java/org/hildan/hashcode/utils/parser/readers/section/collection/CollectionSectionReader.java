package org.hildan.hashcode.utils.parser.readers.section.collection;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.IntFunction;

import org.hildan.hashcode.utils.parser.context.Context;
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

    /**
     * Creates a new {@code CollectionSectionReader}.
     *
     * @param constructor
     *         a constructor to create a new container, given the size as input
     * @param itemReader
     *         a child reader used to read each item
     * @param getSize
     *         a function to get the number of items to read. It is given the parent object and the context as
     *         parameters, so that it can compute a value from the parent or from context variables.
     * @param parentSetter
     *         a setter to update the parent with the created container
     */
    public CollectionSectionReader(IntFunction<C> constructor, ObjectReader<? extends E, ? super P> itemReader,
                                   BiFunction<? super P, Context, Integer> getSize,
                                   BiConsumer<? super P, ? super C> parentSetter) {
        super(constructor, itemReader, getSize, parentSetter);
    }

    @Override
    protected void add(C container, int index, E element) {
        container.add(element);
    }
}
