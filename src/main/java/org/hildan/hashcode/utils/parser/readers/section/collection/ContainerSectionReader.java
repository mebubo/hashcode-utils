package org.hildan.hashcode.utils.parser.readers.section.collection;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.IntFunction;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.config.Config;
import org.hildan.hashcode.utils.parser.context.Context;
import org.hildan.hashcode.utils.parser.readers.ObjectReader;
import org.hildan.hashcode.utils.parser.readers.section.SectionReader;

/**
 * A {@link SectionReader} that creates a container, reads multiple child objects from the input, adding them to the
 * container, and sets the created container on the parent object.
 *
 * @param <E>
 *         the type of the elements in the container
 * @param <C>
 *         the type of the container itself
 * @param <P>
 *         the type of parent on which this {@code ContainerSectionReader} sets the created container
 */
public abstract class ContainerSectionReader<E, C, P> implements SectionReader<P> {

    private final BiConsumer<P, ? super C> parentSetter;

    private final IntFunction<? extends C> constructor;

    private final ObjectReader<E> itemReader;

    private final BiFunction<P, Context, Integer> getSize;

    /**
     * Creates a new {@code ContainerSectionReader} that may read the expected number of items from the parent
     * object or a context variable.
     *
     * @param constructor
     *         a constructor to create a new container, given the size as input
     * @param itemReader
     *         a child reader used to read each item
     * @param getSize
     *         a function to get the number of items to read, which is given the parent object as parameter
     * @param parentSetter
     *         a setter to update the parent with the created container
     */
    public ContainerSectionReader(IntFunction<? extends C> constructor, ObjectReader<E> itemReader,
                                  BiFunction<P, Context, Integer> getSize, BiConsumer<P, ? super C> parentSetter) {
        this.constructor = constructor;
        this.itemReader = itemReader;
        this.getSize = getSize;
        this.parentSetter = parentSetter;
    }

    @Override
    public void readSection(P parent, Context context, Config config) throws InputParsingException {
        int size = getSize.apply(parent, context);
        C collection = constructor.apply(size);
        for (int i = 0; i < size; i++) {
            add(collection, i, itemReader.read(context, config));
        }
        parentSetter.accept(parent, collection);
    }

    /**
     * Adds an element to the created container.
     * <p>
     * This method is guaranteed to be called sequentially in the order of indices from 0 to size - 1. The index
     * parameter is only here to support array-like implementations, but collections may ignore it.
     *
     * @param container
     *         the container to add the element to
     * @param index
     *         the index at which the element should be added
     * @param element
     *         the element to add
     */
    protected abstract void add(C container, int index, E element);
}
