package org.hildan.hashcode.utils.parser.readers.constructors;

/**
 * The type of a constructor with an int array as single argument.
 *
 * @param <T>
 *         the type of objects that this constructor creates
 */
public interface IntArrayConstructor<T> {

    /**
     * Creates a new instance.
     *
     * @param args
     *         some int arguments
     *
     * @return the created object
     */
    T create(int[] args);
}
