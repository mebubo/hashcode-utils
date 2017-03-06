package org.hildan.hashcode.utils.parser.readers.section.collection;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;

import org.hildan.hashcode.utils.parser.Context;
import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.config.Config;
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

    private final BiConsumer<P, C> parentSetter;

    private final IntFunction<C> constructor;

    private final ObjectReader<E> itemReader;

    private final Integer fixedSize;

    private final Function<P, Integer> getSizeFromParent;

    private final String sizeVariable;

    /**
     * Creates a new {@code ContainerSectionReader} with a fixed number of items to read.
     *
     * @param constructor
     *         a constructor to create a new container, given the size as input
     * @param itemReader
     *         a child reader used to read each item
     * @param size
     *         the number of items to read
     * @param parentSetter
     *         a setter to update the parent with the created container
     */
    public ContainerSectionReader(IntFunction<C> constructor, ObjectReader<E> itemReader, Integer size,
            BiConsumer<P, C> parentSetter) {
        this.constructor = constructor;
        this.itemReader = itemReader;
        this.fixedSize = size;
        this.sizeVariable = null;
        this.getSizeFromParent = null;
        this.parentSetter = parentSetter;
    }

    /**
     * Creates a new {@code ContainerSectionReader} reading the expected number of items from a context variable.
     *
     * @param constructor
     *         a constructor to create a new container, given the size as input
     * @param itemReader
     *         a child reader used to read each item
     * @param sizeVariable
     *         a context variable containing the number of items to read
     * @param parentSetter
     *         a setter to update the parent with the created container
     */
    public ContainerSectionReader(IntFunction<C> constructor, ObjectReader<E> itemReader, String sizeVariable,
            BiConsumer<P, C> parentSetter) {
        this.constructor = constructor;
        this.itemReader = itemReader;
        this.fixedSize = null;
        this.sizeVariable = sizeVariable;
        this.getSizeFromParent = null;
        this.parentSetter = parentSetter;
    }

    /**
     * Creates a new {@code ContainerSectionReader} reading the expected number of items from the parent object.
     *
     * @param constructor
     *         a constructor to create a new container, given the size as input
     * @param itemReader
     *         a child reader used to read each item
     * @param getSizeFromParent
     *         a function to get the number of items to read from the parent
     * @param parentSetter
     *         a setter to update the parent with the created container
     */
    public ContainerSectionReader(IntFunction<C> constructor, ObjectReader<E> itemReader,
            Function<P, Integer> getSizeFromParent, BiConsumer<P, C> parentSetter) {
        this.constructor = constructor;
        this.itemReader = itemReader;
        this.fixedSize = null;
        this.sizeVariable = null;
        this.getSizeFromParent = getSizeFromParent;
        this.parentSetter = parentSetter;
    }

    @Override
    public void readSection(P parent, Context context, Config config) throws InputParsingException {
        int size = getSize(parent, context);
        C collection = constructor.apply(size);
        for (int i = 0; i < size; i++) {
            add(collection, i, itemReader.read(context, config));
        }
        parentSetter.accept(parent, collection);
    }

    /**
     * Adds an element to the created container.
     *
     * @param container
     *         the container to add the element to
     * @param index
     *         the index at which the element should be added
     * @param element
     *         the element to add
     */
    protected abstract void add(C container, int index, E element);

    protected int getSize(P objectToFill, Context context) {
        if (fixedSize != null) {
            return fixedSize;
        } else if (getSizeFromParent != null) {
            return getSizeFromParent.apply(objectToFill);
        } else {
            return getSizeFromContext(context);
        }
    }

    private int getSizeFromContext(Context context) {
        String size = context.getVariable(sizeVariable);
        try {
            return Integer.parseInt(size);
        } catch (NumberFormatException e) {
            throw new InputParsingException(
                    "Cannot get the size of the list, value '" + size + "' of variable '" + sizeVariable
                            + "' is not a number");
        }
    }
}
