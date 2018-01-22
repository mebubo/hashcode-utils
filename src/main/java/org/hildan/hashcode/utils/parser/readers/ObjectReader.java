package org.hildan.hashcode.utils.parser.readers;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ObjIntConsumer;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.context.Context;
import org.hildan.hashcode.utils.parser.readers.line.LineReader;
import org.hildan.hashcode.utils.parser.readers.section.FieldAndVarReader;
import org.hildan.hashcode.utils.parser.readers.section.FieldsAndVarsLineReader;
import org.hildan.hashcode.utils.parser.readers.section.SectionReader;
import org.hildan.hashcode.utils.parser.readers.variable.VariableReader;
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

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then executes the given
     * {@link SectionReader} to update it.
     *
     * @param sectionReader
     *         the {@link SectionReader} to execute after this {@link ObjectReader}
     *
     * @return the resulting new {@link ObjectReader}
     */
    default ObjectReader<T> then(SectionReader<? super T> sectionReader) {
        return ctx -> {
            T obj = read(ctx);
            sectionReader.readAndSet(ctx, obj);
            return obj;
        };
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then executes the given
     * function on the context. This can be useful when we simply want to set variables without caring about the created
     * object.
     *
     * @param consumer
     *         the function to execute after this {@link ObjectReader}
     *
     * @return the resulting new {@link ObjectReader}
     */
    default ObjectReader<T> then(Consumer<Context> consumer) {
        return ctx -> {
            T obj = read(ctx);
            consumer.accept(ctx);
            return obj;
        };
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then consumes one token of
     * the input without setting anything.
     *
     * @return the resulting new {@link ObjectReader}
     */
    default ObjectReader<T> skip() {
        return then((ctx, parent) -> ctx.readString());
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then reads one token from the
     * input and stores it into the given variable.
     *
     * @param variableName
     *         the name of the variable to set
     *
     * @return the resulting new {@link ObjectReader}
     */
    default ObjectReader<T> var(String variableName) {
        return then(new VariableReader(variableName));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then reads tokens from the
     * input to store them into the given variables. The number of variable names determines the number of tokens read
     * from the input.
     *
     * @param variableNames
     *         the name of the variables to set
     *
     * @return the resulting new {@link ObjectReader}
     */
    default ObjectReader<T> vars(String... variableNames) {
        return then(new VariableReader(variableNames));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then reads one token from the
     * input to set the given field on the created object. The proper type conversion is done based on the type of the
     * field.
     * <p>
     * WARNING: This method is here to allow brevity but it is sensitive to refactoring as it uses a simple string for
     * the name of the field. You should prefer using setter-based methods like {@link #prop(BiConsumer, Function)},
     * {@link #integer(ObjIntConsumer)} or {@link #string(BiConsumer)}, which are checked at compile time.
     *
     * @param fieldName
     *         the name of the field to set
     *
     * @return the resulting new {@link ObjectReader}
     */
    default ObjectReader<T> field(String fieldName) {
        return then(new FieldAndVarReader<>(fieldName, null));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then reads one token from the
     * input to set the given field on the created object, and stores it in a variable as well. The proper type
     * conversion is done based on the type of the field.
     * <p>
     * WARNING: This method is here to allow brevity but it is sensitive to refactoring as it uses a simple string for
     * the name of the field. You should prefer using setter-based methods like {@link #prop(BiConsumer, Function)},
     * {@link #integer(ObjIntConsumer)} or {@link #string(BiConsumer)}, which are checked at compile time.
     *
     * @param fieldName
     *         the name of the field to set
     * @param variableName
     *         the name of the variable to set
     *
     * @return the resulting new {@link ObjectReader}
     */
    default ObjectReader<T> fieldAndVar(String fieldName, String variableName) {
        return then(new FieldAndVarReader<>(fieldName, variableName));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then reads one token from the
     * input to set a property of the created object.
     *
     * @param setter
     *         the setter to use to set the value on the target object
     * @param converter
     *         a function to convert the string token read from the input into the value to set
     * @param <V>
     *         the type of value that the given converter yields
     *
     * @return the resulting new {@link ObjectReader}
     */
    default <V> ObjectReader<T> prop(BiConsumer<? super T, ? super V> setter, Function<String, V> converter) {
        return then(SectionReader.ofObj(setter, converter));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then reads one int from the
     * input to set a property of the created object.
     *
     * @param setter
     *         the setter to use to set the value on the target object
     *
     * @return the resulting new {@link ObjectReader}
     */
    default ObjectReader<T> integer(ObjIntConsumer<? super T> setter) {
        return then(SectionReader.ofInt(setter));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then reads one string from
     * the input to set a property of the created object.
     *
     * @param setter
     *         the setter to use to set the value on the target object
     *
     * @return the resulting new {@link ObjectReader}
     */
    default ObjectReader<T> string(BiConsumer<? super T, ? super String> setter) {
        return then(SectionReader.ofString(setter));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then maps each element of the
     * next line to a field of the created object or to a context variable, or both.
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
     * @return the resulting new {@link ObjectReader}
     */
    default ObjectReader<T> fieldsAndVarsLine(String... fieldAndVarNames) {
        return then(new FieldsAndVarsLineReader<>(fieldAndVarNames));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then creates an array of ints
     * from the next line, and sets it on the created object using the provided setter.
     *
     * @param setter
     *         the setter to call on the created object, with the created array
     *
     * @return the resulting new {@link ObjectReader}
     */
    default ObjectReader<T> intArrayLine(BiConsumer<? super T, int[]> setter) {
        return then(SectionReader.of(setter, LineReader.ofIntArray()));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then creates an array of
     * longs from the next line, and sets it on the created object using the provided setter.
     *
     * @param setter
     *         the setter to call on the created object, with the created array
     *
     * @return the resulting new {@link ObjectReader}
     */
    default ObjectReader<T> longArrayLine(BiConsumer<? super T, long[]> setter) {
        return then(SectionReader.of(setter, LineReader.ofLongArray()));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then creates an array of
     * doubles from the next line, and sets it on the created object using the provided setter.
     *
     * @param setter
     *         the setter to call on the created object, with the created array
     *
     * @return the resulting new {@link ObjectReader}
     */
    default ObjectReader<T> doubleArrayLine(BiConsumer<? super T, double[]> setter) {
        return then(SectionReader.of(setter, LineReader.ofDoubleArray()));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then creates an array of
     * strings from the next line, and sets it on the created object using the provided setter.
     *
     * @param setter
     *         the setter to call on the created object, with the created array
     *
     * @return the resulting new {@link ObjectReader}
     */
    default ObjectReader<T> stringArrayLine(BiConsumer<? super T, String[]> setter) {
        return then(SectionReader.of(setter, LineReader.ofStringArray()));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then creates an array of
     * objects from the next line, and sets it on the created object using the provided setter.
     *
     * @param setter
     *         the setter to call on the created object, with the created array
     * @param arrayCreator
     *         a function to create a new array, given the desired size
     * @param itemConverter
     *         a function to convert each string element of the line into an element of the array
     * @param <E>
     *         the type of elements in the created array
     *
     * @return the resulting new {@link ObjectReader}
     */
    default <E> ObjectReader<T> arrayLine(BiConsumer<? super T, ? super E[]> setter, IntFunction<E[]> arrayCreator,
            Function<? super String, ? extends E> itemConverter) {
        return then(SectionReader.of(setter, LineReader.ofArray(arrayCreator, itemConverter)));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then creates a list of
     * objects from the next line, and sets it on the created object using the provided setter.
     *
     * @param setter
     *         the setter to call on the created object, with the created list
     * @param itemConverter
     *         a function to convert each string element of the line into an element of the list
     * @param <E>
     *         the type of elements in the created list
     *
     * @return the resulting new {@link ObjectReader}
     */
    default <E> ObjectReader<T> listLine(BiConsumer<? super T, ? super List<E>> setter,
            Function<String, ? extends E> itemConverter) {
        LineReader<List<E>> lineReader = LineReader.ofList(itemConverter);
        return then(SectionReader.of(setter, lineReader));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then creates an array of
     * objects from the next N lines, and sets it on the created object using the provided setter. N will be read at
     * parsing time from the current value of the given context variable, which needs to be previously set. A variable
     * can be set, for instance, using {@link #var(String)} or {@link #vars(String...)}.
     *
     * @param setter
     *         the setter to call on the created object, with the created array
     * @param arrayCreator
     *         a function to create a new array, given the desired size
     * @param sizeVariable
     *         a context variable that will contain the desired size of the array
     * @param itemReader
     *         a child reader used to read each item
     * @param <E>
     *         the type of elements in the created array
     *
     * @return the resulting new {@link ObjectReader}
     */
    default <E> ObjectReader<T> array(BiConsumer<? super T, ? super E[]> setter, IntFunction<E[]> arrayCreator,
            String sizeVariable, ChildReader<? extends E, ? super T> itemReader) {
        return then(
                SectionReader.ofArray(setter, arrayCreator, (p, c) -> c.getVariableAsInt(sizeVariable), itemReader));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then creates an array of
     * objects from the next N lines, and sets it on the created object using the provided setter. N will be read at
     * parsing time by calling the provided getSize function.
     *
     * @param setter
     *         the setter to call on the created object, with the created array
     * @param arrayCreator
     *         a function to create a new array, given the desired size
     * @param getSize
     *         a function to get the size of the array to create. It takes the created object as parameter.
     * @param itemReader
     *         a child reader used to read each item
     * @param <E>
     *         the type of elements in the created array
     *
     * @return the resulting new {@link ObjectReader}
     */
    default <E> ObjectReader<T> array(BiConsumer<? super T, ? super E[]> setter, IntFunction<E[]> arrayCreator,
            Function<? super T, Integer> getSize, ChildReader<? extends E, ? super T> itemReader) {
        return then(SectionReader.ofArray(setter, arrayCreator, (p, c) -> getSize.apply(p), itemReader));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then creates an array of
     * objects from the next N lines, and set it on the created object using the provided setter. N will be computed at
     * parsing time by calling the provided getSize function.
     *
     * @param setter
     *         the setter to call on the created object, with the created array
     * @param arrayCreator
     *         a function to create a new array, given the desired size
     * @param getSize
     *         a function to get the size of the array to create. It takes the created object as parameter, as well as
     *         the current {@link Context}
     * @param itemReader
     *         a child reader used to read each item
     * @param <E>
     *         the type of elements in the created array
     *
     * @return the resulting new {@link ObjectReader}
     */
    default <E> ObjectReader<T> array(BiConsumer<? super T, ? super E[]> setter, IntFunction<E[]> arrayCreator,
            BiFunction<? super T, Context, Integer> getSize, ChildReader<? extends E, ? super T> itemReader) {
        return then(SectionReader.ofArray(setter, arrayCreator, getSize, itemReader));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then creates a list of
     * objects from the next N lines, and sets it on the created object using the provided setter. N will be read at
     * parsing time from the current value of the given context variable, which needs to be previously set. A variable
     * can be set, for instance, using {@link #fieldsAndVarsLine(String...)}.
     *
     * @param setter
     *         the setter to call on the created object, with the created list
     * @param sizeVariable
     *         a context variable that will contain the number of elements to read and put in the list
     * @param itemReader
     *         a child reader used to read each item
     * @param <E>
     *         the type of elements in the created list
     *
     * @return the resulting new {@link ObjectReader}
     */
    default <E> ObjectReader<T> list(BiConsumer<? super T, ? super List<E>> setter, String sizeVariable,
            ChildReader<? extends E, ? super T> itemReader) {
        return then(SectionReader.ofList(setter, (p, c) -> c.getVariableAsInt(sizeVariable), itemReader));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then creates a list of
     * objects from the next N lines, and sets it on the created object using the provided setter. N will be read at
     * parsing time by calling the provided getSize function.
     *
     * @param setter
     *         the setter to call on the created object, with the created list
     * @param getSize
     *         a function to get the size of the array to create. It takes the created object as parameter.
     * @param itemReader
     *         a child reader used to read each item
     * @param <E>
     *         the type of elements in the created list
     *
     * @return the resulting new {@link ObjectReader}
     */
    default <E> ObjectReader<T> list(BiConsumer<? super T, ? super List<E>> setter,
            Function<? super T, Integer> getSize, ChildReader<? extends E, ? super T> itemReader) {
        return then(SectionReader.ofList(setter, (p, c) -> getSize.apply(p), itemReader));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then creates a list of
     * objects from the next N lines, and sets it on the created object using the provided setter. N will be computed at
     * parsing time by calling the provided getSize function.
     *
     * @param setter
     *         the setter to call on the created object, with the created list
     * @param getSize
     *         a function to get the size of the array to create. It takes the created object as parameter, as well as
     *         the current {@link Context}
     * @param itemReader
     *         a child reader used to read each item
     * @param <E>
     *         the type of elements in the created list
     *
     * @return the resulting new {@link ObjectReader}
     */
    default <E> ObjectReader<T> list(BiConsumer<? super T, ? super List<E>> setter,
            BiFunction<? super T, Context, Integer> getSize, ChildReader<? extends E, ? super T> itemReader) {
        return then(SectionReader.ofList(setter, getSize, itemReader));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then creates a child object
     * from the next few lines using the given reader, and sets it on the created parent object using the given setter.
     * The childReader will read as much input as necessary.
     *
     * @param setter
     *         the setter to call on the parent created object, with the created child object
     * @param childReader
     *         a reader used to read the child object
     * @param <C>
     *         the type of the child object to create
     *
     * @return the resulting new {@link ObjectReader}
     */
    default <C> ObjectReader<T> child(BiConsumer<? super T, ? super C> setter,
            ChildReader<C, ? super T> childReader) {
        return then(SectionReader.of(setter, childReader));
    }

}
