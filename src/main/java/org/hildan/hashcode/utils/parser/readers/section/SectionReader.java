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
 * A reader that consumes as much input as necessary to update part of (a "section" of) its parent.
 * <p>
 * For instance, this may be used to read an object that is a field of the parent class and assign the parent's field
 * with the result.
 *
 * @param <P>
 *         the type of parent that this {@code SectionReader} can update
 */
public interface SectionReader<P> {

    /**
     * Reads as much input as necessary to update a section of the given parent object.
     *
     * @param context
     *         the context to read from
     * @param parent
     *         the parent object to update
     *
     * @throws InputParsingException
     *         if something went wrong while reading the input
     */
    void readAndSet(@NotNull Context context, @NotNull P parent) throws InputParsingException;

    static <T, P> SectionReader<P> of(BiConsumer<? super P, ? super T> parentSetter,
                                      ChildReader<? extends T, ? super P> itemReader) {
        return new ObjectSectionReader<>(itemReader, parentSetter);
    }

    static <E, P> SectionReader<P> array(BiConsumer<? super P, ? super E[]> parentSetter, IntFunction<E[]> arrayCreator,
                                         BiFunction<? super P, Context, Integer> getSize,
                                         ChildReader<? extends E, ? super P> itemReader) {
        return of(parentSetter, ContainerReader.array(arrayCreator, getSize, itemReader));
    }

    static <E, P> SectionReader<P> list(BiConsumer<? super P, ? super List<E>> parentSetter,
                                        BiFunction<? super P, Context, Integer> getSize,
                                        ChildReader<? extends E, ? super P> itemReader) {
        ChildReader<List<E>, P> listReader = ContainerReader.list(getSize, itemReader);
        return of(parentSetter, listReader);
    }

    static <E, C extends Collection<E>, P> SectionReader<P> collection(BiConsumer<? super P, ? super C> parentSetter,
                                                                       IntFunction<C> constructor,
                                                                       BiFunction<? super P, Context, Integer> getSize,
                                                                       ChildReader<? extends E, ? super P>
                                                                               itemReader) {
        return of(parentSetter, ContainerReader.collection(constructor, getSize, itemReader));
    }
}
