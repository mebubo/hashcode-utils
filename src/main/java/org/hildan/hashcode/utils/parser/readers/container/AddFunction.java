package org.hildan.hashcode.utils.parser.readers.container;

@FunctionalInterface
public interface AddFunction<E, C> {

    /**
     * Adds an element to the given container.
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
    void apply(C container, int index, E element);
}
