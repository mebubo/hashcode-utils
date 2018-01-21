package org.hildan.hashcode.utils.parser.readers;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.context.Context;
import org.hildan.hashcode.utils.parser.readers.line.LineReader;
import org.hildan.hashcode.utils.parser.readers.section.FieldAndVarReader;
import org.hildan.hashcode.utils.parser.readers.section.FieldsAndVarsLineReader;
import org.hildan.hashcode.utils.parser.readers.section.SectionReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Reads an object from the current {@link Context}, consuming as much input as necessary. An {@code ObjectReader}
 * creates an object that is either the root of the object tree, or is independent from the parent object within which
 * it's being constructed.
 * <p>
 * This is anyway an extension of {@link ChildReader}, because it can be constructed in the context of a parent object,
 * but simply doesn't care about its parent.
 *
 * @param <T>
 *         the type of object this {@code ObjectReader} creates
 */
@FunctionalInterface
public interface ObjectReader<T> extends ChildReader<T, Object>, Function<Context, T> {

    /**
     * Reads an object from the given {@link Context}, consuming as much input as necessary.
     *
     * @param context
     *         the context to read lines from
     *
     * @return the created object, may be null
     *
     * @throws InputParsingException
     *         if something went wrong when reading the input
     */
    @Nullable T read(@NotNull Context context) throws InputParsingException;

    @Override
    @Nullable
    default T read(@NotNull Context context, @Nullable Object parent) throws InputParsingException {
        // the parent is ignored because this kind of readers doesn't need it
        return read(context);
    }

    @Override
    default T apply(Context context) throws InputParsingException {
        return read(context);
    }

    default ObjectReader<T> then(SectionReader<T> sectionReader) {
        return ctx -> {
            T obj = read(ctx);
            sectionReader.readAndSet(ctx, obj);
            return obj;
        };
    }

    /**
     * Tells this reader to map each element of the next line to a field of the created object or to a context variable,
     * or both.
     * <p>
     * The field/variable names are given as strings that can each be one of: <ul> <li>a field name (e.g.
     * "myField1")</li> <li>a '@' symbol followed by a variable name (e.g. "@N", "@myVar", "@123"...)</li> <li>both a
     * field name and a variable name separated by a '@' (e.g. "nItems@N", "size@nbOfSatellites"...)</li> </ul>
     * <p>
     * Note that "" describe neither a field nor a variable, and thus the corresponding entry in the line will be
     * ignored during parsing. Null descriptions and descriptions ending in '@' are forbidden.
     *
     * @param fieldAndVarNames
     *         an array of field/variable names, as described above
     *
     * @return this {@code ObjectReader}, for a convenient configuration syntax
     */
    default ObjectReader<T> fieldsAndVarsLine(String... fieldAndVarNames) {
        return then(new FieldsAndVarsLineReader<>(fieldAndVarNames));
    }

    /**
     * Reads one token and sets the given field on the object created by this reader.
     *
     * @param fieldName
     *         the name of the field to set
     *
     * @return this {@code ObjectReader}, for a convenient configuration syntax
     */
    default ObjectReader<T> field(String fieldName) {
        return then(new FieldAndVarReader<>(fieldName, null));
    }

    /**
     * Reads one token and sets the given context variable with the value.
     *
     * @param variableName
     *         the name of the variable to set.
     *
     * @return this {@code ObjectReader}, for a convenient configuration syntax
     */
    default ObjectReader<T> var(String variableName) {
        return then(new FieldAndVarReader<>(null, variableName));
    }

    /**
     * Reads one token and sets both the given field and the given context variable with the value.
     *
     * @param fieldName
     *         the name of the field to set
     * @param variableName
     *         the name of the variable to set
     *
     * @return this {@code ObjectReader}, for a convenient configuration syntax
     */
    default ObjectReader<T> fieldAndVar(String fieldName, String variableName) {
        return then(new FieldAndVarReader<>(fieldName, variableName));
    }

    /**
     * Consumes one token of the input without setting anything.
     *
     * @return this {@code ObjectReader}, for a convenient configuration syntax
     */
    default ObjectReader<T> skip() {
        return then((ctx, parent) -> ctx.readString());
    }

    /**
     * Tells this reader to create an array of ints from the next line, and set it on the object being created using the
     * provided setter.
     *
     * @param setter
     *         the setter to call on the object being created, with the created array
     *
     * @return this {@code ObjectReader}, for a convenient configuration syntax
     */
    default ObjectReader<T> intArrayLine(BiConsumer<? super T, int[]> setter) {
        return then(SectionReader.of(setter, LineReader.ofIntArray()));
    }

    /**
     * Tells this reader to create an array of longs from the next line, and set it on the object being created using
     * the provided setter.
     *
     * @param setter
     *         the setter to call on the object being created, with the created array
     *
     * @return this {@code ObjectReader}, for a convenient configuration syntax
     */
    default ObjectReader<T> longArrayLine(BiConsumer<? super T, long[]> setter) {
        return then(SectionReader.of(setter, LineReader.ofLongArray()));
    }

    /**
     * Tells this reader to create an array of doubles from the next line, and set it on the object being created using
     * the provided setter.
     *
     * @param setter
     *         the setter to call on the object being created, with the created array
     *
     * @return this {@code ObjectReader}, for a convenient configuration syntax
     */
    default ObjectReader<T> doubleArrayLine(BiConsumer<? super T, double[]> setter) {
        return then(SectionReader.of(setter, LineReader.ofDoubleArray()));
    }

    /**
     * Tells this reader to create an array of strings from the next line, and set it on the object being created using
     * the provided setter.
     *
     * @param setter
     *         the setter to call on the object being created, with the created array
     *
     * @return this {@code ObjectReader}, for a convenient configuration syntax
     */
    default ObjectReader<T> stringArrayLine(BiConsumer<? super T, String[]> setter) {
        return then(SectionReader.of(setter, LineReader.ofStringArray()));
    }

    /**
     * Tells this reader to create an array of objects from the next line, and set it on the object being created using
     * the provided setter.
     *
     * @param setter
     *         the setter to call on the object being created, with the created array
     * @param arrayCreator
     *         a function to create a new array, given the desired size
     * @param itemConverter
     *         a function to convert each string element of the line into an element of the array
     * @param <E>
     *         the type of elements in the created array
     *
     * @return this {@code ObjectReader}, for a convenient configuration syntax
     */
    default <E> ObjectReader<T> arrayLine(BiConsumer<? super T, ? super E[]> setter, IntFunction<E[]> arrayCreator,
            Function<? super String, ? extends E> itemConverter) {
        return then(SectionReader.of(setter, LineReader.ofArray(arrayCreator, itemConverter)));
    }

    /**
     * Tells this reader to create a list of objects from the next line, and set it on the object being created using
     * the provided setter.
     *
     * @param setter
     *         the setter to call on the object being created, with the created list
     * @param itemConverter
     *         a function to convert each string element of the line into an element of the list
     * @param <E>
     *         the type of elements in the created list
     *
     * @return this {@code ObjectReader}, for a convenient configuration syntax
     */
    default <E> ObjectReader<T> listLine(BiConsumer<? super T, ? super List<E>> setter,
            Function<String, ? extends E> itemConverter) {
        LineReader<List<E>> lineReader = LineReader.ofList(itemConverter);
        return then(SectionReader.of(setter, lineReader));
    }

    /**
     * Tells this reader to create an array of objects from the next N lines, and set it on the object being created
     * using the provided setter. N will be read at parsing time from the current value of the given context variable,
     * which needs to be previously set. A variable can be set, for instance, using {@link
     * #fieldsAndVarsLine(String...)}.
     *
     * @param setter
     *         the setter to call on the object being created, with the created array
     * @param arrayCreator
     *         a function to create a new array, given the desired size
     * @param sizeVariable
     *         a context variable that will contain the desired size of the array
     * @param itemReader
     *         a child reader used to read each item
     * @param <E>
     *         the type of elements in the created array
     *
     * @return this {@code ObjectReader}, for a convenient configuration syntax
     */
    default <E> ObjectReader<T> array(BiConsumer<? super T, ? super E[]> setter, IntFunction<E[]> arrayCreator,
            String sizeVariable, ChildReader<? extends E, ? super T> itemReader) {
        return then(
                SectionReader.ofArray(setter, arrayCreator, (p, c) -> c.getVariableAsInt(sizeVariable), itemReader));
    }

    /**
     * Tells this reader to create an array of objects from the next N lines, and set it on the object being created
     * using the provided setter. N will be read at parsing time by calling the provided getSize function.
     *
     * @param setter
     *         the setter to call on the object being created, with the created array
     * @param arrayCreator
     *         a function to create a new array, given the desired size
     * @param getSize
     *         a function to get the size of the array to create. It takes the object being created as parameter.
     * @param itemReader
     *         a child reader used to read each item
     * @param <E>
     *         the type of elements in the created array
     *
     * @return this {@code ObjectReader}, for a convenient configuration syntax
     */
    default <E> ObjectReader<T> array(BiConsumer<? super T, ? super E[]> setter, IntFunction<E[]> arrayCreator,
            Function<? super T, Integer> getSize, ChildReader<? extends E, ? super T> itemReader) {
        return then(SectionReader.ofArray(setter, arrayCreator, (p, c) -> getSize.apply(p), itemReader));
    }

    /**
     * Tells this reader to create an array of objects from the next N lines, and set it on the object being created
     * using the provided setter. N will be computed at parsing time by calling the provided getSize function.
     *
     * @param setter
     *         the setter to call on the object being created, with the created array
     * @param arrayCreator
     *         a function to create a new array, given the desired size
     * @param getSize
     *         a function to get the size of the array to create. It takes the object being created as parameter, as
     *         well as the current {@link Context}
     * @param itemReader
     *         a child reader used to read each item
     * @param <E>
     *         the type of elements in the created array
     *
     * @return this {@code ObjectReader}, for a convenient configuration syntax
     */
    default <E> ObjectReader<T> array(BiConsumer<? super T, ? super E[]> setter, IntFunction<E[]> arrayCreator,
            BiFunction<? super T, Context, Integer> getSize, ChildReader<? extends E, ? super T> itemReader) {
        return then(SectionReader.ofArray(setter, arrayCreator, getSize, itemReader));
    }

    /**
     * Tells this reader to create a list of objects from the next N lines, and set it on the object being created
     * using the provided setter. N will be read at parsing time from the current value of the given context variable,
     * which needs to be previously set. A variable can be set, for instance, using {@link
     * #fieldsAndVarsLine(String...)}.
     *
     * @param setter
     *         the setter to call on the object being created, with the created list
     * @param sizeVariable
     *         a context variable that will contain the number of elements to read and put in the list
     * @param itemReader
     *         a child reader used to read each item
     * @param <E>
     *         the type of elements in the created list
     *
     * @return this {@code ObjectReader}, for a convenient configuration syntax
     */
    default <E> ObjectReader<T> list(BiConsumer<? super T, ? super List<E>> setter, String sizeVariable,
            ChildReader<? extends E, ? super T> itemReader) {
        return then(SectionReader.ofList(setter, (p, c) -> c.getVariableAsInt(sizeVariable), itemReader));
    }

    /**
     * Tells this reader to create a list of objects from the next N lines, and set it on the object being created
     * using the provided setter. N will be read at parsing time by calling the provided getSize function.
     *
     * @param setter
     *         the setter to call on the object being created, with the created list
     * @param getSize
     *         a function to get the size of the array to create. It takes the object being created as parameter.
     * @param itemReader
     *         a child reader used to read each item
     * @param <E>
     *         the type of elements in the created list
     *
     * @return this {@code ObjectReader}, for a convenient configuration syntax
     */
    default <E> ObjectReader<T> list(BiConsumer<? super T, ? super List<E>> setter,
            Function<? super T, Integer> getSize, ChildReader<? extends E, ? super T> itemReader) {
        return then(SectionReader.ofList(setter, (p, c) -> getSize.apply(p), itemReader));
    }

    /**
     * Tells this reader to create a list of objects from the next N lines, and set it on the object being created
     * using the provided setter. N will be computed at parsing time by calling the provided getSize function.
     *
     * @param setter
     *         the setter to call on the object being created, with the created list
     * @param getSize
     *         a function to get the size of the array to create. It takes the object being created as parameter, as
     *         well as the current {@link Context}
     * @param itemReader
     *         a child reader used to read each item
     * @param <E>
     *         the type of elements in the created list
     *
     * @return this {@code ObjectReader}, for a convenient configuration syntax
     */
    default <E> ObjectReader<T> list(BiConsumer<? super T, ? super List<E>> setter,
            BiFunction<? super T, Context, Integer> getSize, ChildReader<? extends E, ? super T> itemReader) {
        return then(SectionReader.ofList(setter, getSize, itemReader));
    }

    /**
     * Tells this reader to create an object from the next few lines using the given reader, and set it on the object
     * being created using the given setter. The childReader will read as much input as necessary.
     *
     * @param setter
     *         the setter to call on the parent object being created, with the created child object
     * @param childReader
     *         a reader used to read the child object
     * @param <C>
     *         the type of object to create
     *
     * @return this {@code ObjectReader}, for a convenient configuration syntax
     */
    default <C> ObjectReader<T> objectSection(BiConsumer<? super T, ? super C> setter,
            ChildReader<? extends C, ? super T> childReader) {
        return then(SectionReader.of(setter, childReader));
    }

}
