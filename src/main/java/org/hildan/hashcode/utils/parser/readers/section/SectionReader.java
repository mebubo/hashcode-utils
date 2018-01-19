package org.hildan.hashcode.utils.parser.readers.section;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.IntFunction;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.context.Context;
import org.hildan.hashcode.utils.parser.readers.ChildReader;
import org.hildan.hashcode.utils.parser.readers.container.ContainerReader;
import org.jetbrains.annotations.NotNull;

/**
 * A reader that consumes as much input as necessary (a possibly multi-line "section") to update a previously created
 * object.
 * <p>
 * For instance, this may be used to read an object from the input and set it as a field/property of a parent object.
 *
 * @param <T>
 *         the type of object that this {@code SectionReader} can update
 */
@FunctionalInterface
public interface SectionReader<T> {

    /**
     * Reads as much input as necessary to update the given object.
     *
     * @param context
     *         the context to read from
     * @param object
     *         the object to update
     *
     * @throws InputParsingException
     *         if something went wrong while reading the input
     */
    void readAndSet(@NotNull Context context, @NotNull T object) throws InputParsingException;

    /**
     * Returns a {@link SectionReader} that reads a value and sets it on the target object, consuming as much input
     * as necessary.
     *
     * @param setter
     *         the setter to use to set the value on the target object
     * @param valueReader
     *         the reader to use to read the value to set
     * @param <V>
     *         the type of the value to set, which the given {@link ChildReader} creates
     * @param <T>
     *         the type of object that the returned {@code SectionReader} updates
     *
     * @return a {@link SectionReader} that reads a value and sets it on the target object
     */
    static <V, T> SectionReader<T> of(BiConsumer<? super T, ? super V> setter,
            ChildReader<? extends V, ? super T> valueReader) {
        return (ctx, obj) -> setter.accept(obj, valueReader.read(ctx, obj));
    }

    static <E, P> SectionReader<P> ofArray(BiConsumer<? super P, ? super E[]> setter, IntFunction<E[]> arrayCreator,
            BiFunction<? super P, Context, Integer> getSize, ChildReader<? extends E, ? super P> itemReader) {
        return of(setter, ContainerReader.ofArray(arrayCreator, getSize, itemReader));
    }

    static <E, P> SectionReader<P> ofList(BiConsumer<? super P, ? super List<E>> setter,
            BiFunction<? super P, Context, Integer> getSize, ChildReader<? extends E, ? super P> itemReader) {
        ChildReader<List<E>, P> listReader = ContainerReader.ofList(getSize, itemReader);
        return of(setter, listReader);
    }

    static <E, C extends Collection<E>, P> SectionReader<P> ofCollection(BiConsumer<? super P, ? super C> setter,
            IntFunction<C> constructor, BiFunction<? super P, Context, Integer> getSize,
            ChildReader<? extends E, ? super P> itemReader) {
        return of(setter, ContainerReader.ofCollection(constructor, getSize, itemReader));
    }
}
