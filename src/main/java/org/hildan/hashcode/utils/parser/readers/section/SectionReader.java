package org.hildan.hashcode.utils.parser.readers.section;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjIntConsumer;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.context.Context;
import org.hildan.hashcode.utils.parser.readers.ChildReader;
import org.hildan.hashcode.utils.parser.readers.container.ContainerReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A reader that consumes as much input as necessary (a possibly multi-line "section") to update a previously created
 * object. <p> For instance, this may be used to read an object from the input and set it as a field/property of a
 * parent object.
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
    void readAndSet(@NotNull Context context, @Nullable T object) throws InputParsingException;

    /**
     * Returns a {@link SectionReader} that reads a string and sets it on the target object, consuming as much input as
     * necessary.
     *
     * @param setter
     *         the setter to use to set the value on the target object
     * @param <T>
     *         the type of object that the returned {@code SectionReader} updates
     *
     * @return a {@link SectionReader} that reads a value and sets it on the target object
     */
    static <T> SectionReader<T> settingString(BiConsumer<T, ? super String> setter) {
        return (ctx, obj) -> setter.accept(obj, ctx.readString());
    }

    /**
     * Returns a {@link SectionReader} that reads an int and sets it on the target object, consuming as much input as
     * necessary.
     *
     * @param setter
     *         the setter to use to set the value on the target object
     * @param <T>
     *         the type of object that the returned {@code SectionReader} updates
     *
     * @return a {@link SectionReader} that reads a value and sets it on the target object
     */
    static <T> SectionReader<T> settingInt(ObjIntConsumer<T> setter) {
        return (ctx, obj) -> setter.accept(obj, ctx.readInt());
    }

    /**
     * Returns a {@link SectionReader} that reads a double and sets it on the target object, consuming as much input as
     * necessary.
     *
     * @param setter
     *         the setter to use to set the value on the target object
     * @param <T>
     *         the type of object that the returned {@code SectionReader} updates
     *
     * @return a {@link SectionReader} that reads a value and sets it on the target object
     */
    static <T> SectionReader<T> settingDouble(ObjDoubleConsumer<T> setter) {
        return (ctx, obj) -> setter.accept(obj, ctx.readDouble());
    }

    /**
     * Returns a {@link SectionReader} that reads a value and sets it on the target object, consuming as much input as
     * necessary.
     *
     * @param setter
     *         the setter to use to set the value on the target object
     * @param valueConverter
     *         a function to convert the string token read from the input into the value to set
     * @param <V>
     *         the type of value that the given converter yields
     * @param <T>
     *         the type of object that the returned {@code SectionReader} updates
     *
     * @return a {@link SectionReader} that reads a value and sets it on the target object
     */
    static <V, T> SectionReader<T> settingObject(BiConsumer<T, ? super V> setter,
            Function<? super String, V> valueConverter) {
        return (ctx, obj) -> setter.accept(obj, valueConverter.apply(ctx.readString()));
    }

    /**
     * Returns a {@link SectionReader} that reads a child object and sets it on the target object, consuming as much
     * input as necessary.
     *
     * @param setter
     *         the setter to use to set the value on the target object
     * @param valueReader
     *         the reader to use to read the child object to set
     * @param <V>
     *         the type of the child object to set, which the given {@link ChildReader} creates
     * @param <T>
     *         the type of object that the returned {@code SectionReader} updates
     *
     * @return a {@link SectionReader} that reads a value and sets it on the target object
     */
    static <V, T> SectionReader<T> settingChild(BiConsumer<? super T, ? super V> setter,
            ChildReader<? extends V, ? super T> valueReader) {
        return (ctx, obj) -> setter.accept(obj, valueReader.read(ctx, obj));
    }

    static <E, P> SectionReader<P> settingArray(BiConsumer<? super P, ? super E[]> setter,
            IntFunction<E[]> arrayCreator, BiFunction<? super P, Context, Integer> getSize,
            ChildReader<? extends E, ? super P> itemReader) {
        return settingChild(setter, ContainerReader.ofArray(getSize, itemReader, arrayCreator));
    }

    static <E, P> SectionReader<P> settingList(BiConsumer<? super P, ? super List<E>> setter,
            BiFunction<? super P, Context, Integer> getSize, ChildReader<? extends E, ? super P> itemReader) {
        ChildReader<List<E>, P> listReader = ContainerReader.ofList(getSize, itemReader);
        return settingChild(setter, listReader);
    }

    static <E, C extends Collection<E>, P> SectionReader<P> settingCollection(BiConsumer<? super P, ? super C> setter,
            IntFunction<C> constructor, BiFunction<? super P, Context, Integer> getSize,
            ChildReader<? extends E, ? super P> itemReader) {
        return settingChild(setter, ContainerReader.ofCollection(getSize, itemReader, constructor));
    }
}
