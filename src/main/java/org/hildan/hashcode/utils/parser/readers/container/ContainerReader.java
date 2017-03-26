package org.hildan.hashcode.utils.parser.readers.container;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.IntFunction;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.context.Context;
import org.hildan.hashcode.utils.parser.readers.ObjectReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link ObjectReader} that creates a container, and reads multiple child objects from the input, adding them to the
 * container.
 *
 * @param <E>
 *         the type of the elements in the container
 * @param <C>
 *         the type of the container itself
 * @param <P>
 *         the type of parent on which this {@code ContainerReader} sets the created container
 */
public class ContainerReader<E, C, P> implements ObjectReader<C, P> {

    private final IntFunction<? extends C> constructor;

    private final ObjectReader<? extends E, ? super P> itemReader;

    private final BiFunction<? super P, Context, Integer> getSize;

    private final AddFunction<? super E, ? super C> addFunction;

    /**
     * Creates a new {@code ContainerReader} that may read the expected number of items from the parent object or a
     * context variable.
     *
     * @param constructor
     *         a constructor to create a new container, given the size as input
     * @param getSize
     *         a function to get the number of items to read, which is given the parent object and context as parameter.
     *         Note that the given parent parameter may be null if this reader is called to create a root object.
     * @param addFunction
     *         a function to add elements to the created container
     * @param itemReader
     *         a child reader used to read each item
     */
    public ContainerReader(IntFunction<? extends C> constructor, BiFunction<? super P, Context, Integer> getSize,
                           AddFunction<? super E, ? super C> addFunction,
                           ObjectReader<? extends E, ? super P> itemReader) {
        this.constructor = constructor;
        this.itemReader = itemReader;
        this.getSize = getSize;
        this.addFunction = addFunction;
    }

    @Override
    public C read(@NotNull Context context, @Nullable P parent) throws InputParsingException {
        int size = getSize.apply(parent, context);
        C collection = constructor.apply(size);
        for (int i = 0; i < size; i++) {
            addFunction.apply(collection, i, itemReader.read(context, parent));
        }
        return collection;
    }

    public static <E, P> ObjectReader<E[], P> array(IntFunction<E[]> arrayCreator,
                                                    BiFunction<? super P, Context, Integer> getSize,
                                                    ObjectReader<? extends E, ? super P> itemReader) {
        return new ContainerReader<>(arrayCreator, getSize, Array::set, itemReader);
    }

    public static <E, P> ObjectReader<List<E>, P> list(BiFunction<? super P, Context, Integer> getSize,
                                                       ObjectReader<? extends E, ? super P> itemReader) {
        return new ContainerReader<>(ArrayList::new, getSize, (c, i, e) -> c.add(e), itemReader);
    }

    public static <E, C extends Collection<E>, P> ObjectReader<C, P> collection(IntFunction<C> constructor,
                                                                                BiFunction<? super P, Context,
                                                                                        Integer> getSize,
                                                                                ObjectReader<? extends E, ? super P>
                                                                                        itemReader) {
        return new ContainerReader<>(constructor, getSize, (c, i, e) -> c.add(e), itemReader);
    }
}
