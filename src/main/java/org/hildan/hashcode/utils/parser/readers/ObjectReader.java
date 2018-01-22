package org.hildan.hashcode.utils.parser.readers;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjIntConsumer;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.context.Context;
import org.hildan.hashcode.utils.parser.readers.line.LineReader;
import org.hildan.hashcode.utils.parser.readers.section.FieldAndVarReader;
import org.hildan.hashcode.utils.parser.readers.section.FieldsAndVarsReader;
import org.hildan.hashcode.utils.parser.readers.section.SectionReader;
import org.hildan.hashcode.utils.parser.readers.variable.VariableReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Reads an object from the current {@link Context}, consuming as much input as necessary. An {@code ObjectReader}
 * creates an object that is either the root of the object tree, or is independent from the parent object within which
 * it's being constructed. <p> This is anyway an extension of {@link ChildReader}, because it can be constructed in the
 * context of a parent object, but simply doesn't care about its parent.
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
    @Nullable
    T read(@NotNull Context context) throws InputParsingException;

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
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then consumes n tokens of the
     * input without setting anything.
     *
     * @param n
     *         the number of tokens to skip
     *
     * @return the resulting new {@link ObjectReader}
     */
    default ObjectReader<T> thenSkip(int n) {
        return then(ctx -> ctx.skip(n));
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
    default ObjectReader<T> thenVar(String variableName) {
        return thenVars(variableName);
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
    default ObjectReader<T> thenVars(String... variableNames) {
        return then(new VariableReader(variableNames));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then reads one token from the
     * input to set the given field on the created object. The proper type conversion is done based on the type of the
     * field.
     * <p>
     * WARNING: This method is here to allow brevity but it is sensitive to refactoring as it uses a simple string for
     * the name of the field. Please prefer using setter-based methods like {@link #thenObject(BiConsumer, Function)},
     * {@link #thenInt(ObjIntConsumer)} or {@link #thenString(BiConsumer)}, which are checked at compile time.
     *
     * @param fieldName
     *         the name of the field to set
     *
     * @return the resulting new {@link ObjectReader}
     */
    default ObjectReader<T> thenField(String fieldName) {
        return then(new FieldAndVarReader<>(fieldName, null));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then reads tokens from the
     * input to set the given fields on the created object. The proper type conversion is done based on the type of the
     * fields. The number of field names determines the number of tokens read from the input.
     * <p>
     * WARNING: This method is here to allow brevity but it is sensitive to refactoring as it uses simple strings for
     * the name of the fields. Please prefer using setter-based methods like {@link #thenObject(BiConsumer, Function)},
     * {@link #thenInt(ObjIntConsumer)} or {@link #thenString(BiConsumer)}, which are checked at compile time.
     *
     * @param fieldNames
     *         the names of the fields to set
     *
     * @return the resulting new {@link ObjectReader}
     */
    default ObjectReader<T> thenFields(String... fieldNames) {
        if (Arrays.stream(fieldNames).anyMatch(s -> s.contains("@"))) {
            throw new IllegalArgumentException("Some field names contain the illegal character '@'");
        }
        return thenFieldsAndVars(fieldNames);
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then reads one token from the
     * input to set the given field on the created object, and stores it in a variable as well. The proper type
     * conversion is done based on the type of the field.
     * <p>
     * WARNING: This method is here to allow brevity but it is sensitive to refactoring as it uses a simple string for
     * the name of the field. Please prefer using setter-based methods like {@link #thenObject(BiConsumer, Function)},
     * {@link #thenInt(ObjIntConsumer)} or {@link #thenString(BiConsumer)}, which are checked at compile time.
     *
     * @param fieldName
     *         the name of the field to set
     * @param variableName
     *         the name of the variable to set
     *
     * @return the resulting new {@link ObjectReader}
     */
    default ObjectReader<T> thenFieldAndVar(String fieldName, String variableName) {
        return then(new FieldAndVarReader<>(fieldName, variableName));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then maps each element of the
     * next line to a field of the created object or to a context variable, or both.
     * <p>
     * The field/variable names are given as strings that can each be one of:
     * <ul> <li>a field name (e.g. "myField1")</li> <li>a '@' symbol followed
     * by a variable name (e.g. "@N", "@myVar", "@123"...)</li> <li>both a field name and a variable name separated by a
     * '@' (e.g. "nItems@N", "size@nbOfSatellites"...)</li> </ul> <p> Note that "" describe neither a field nor a
     * variable, and thus the corresponding entry in the line will be ignored during parsing. Null descriptions and
     * descriptions ending in '@' are forbidden.
     *
     * @param fieldAndVarNames
     *         an array of field/variable names, as described above
     *
     * @return the resulting new {@link ObjectReader}
     */
    default ObjectReader<T> thenFieldsAndVars(String... fieldAndVarNames) {
        return then(new FieldsAndVarsReader<>(fieldAndVarNames));
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
    default <V> ObjectReader<T> thenObject(BiConsumer<? super T, ? super V> setter,
            Function<? super String, V> converter) {
        return then(SectionReader.settingObject(setter, converter));
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
    default ObjectReader<T> thenInt(ObjIntConsumer<? super T> setter) {
        return then(SectionReader.settingInt(setter));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then reads one double from
     * the input to set a property of the created object.
     *
     * @param setter
     *         the setter to use to set the value on the target object
     *
     * @return the resulting new {@link ObjectReader}
     */
    default ObjectReader<T> thenDouble(ObjDoubleConsumer<? super T> setter) {
        return then(SectionReader.settingDouble(setter));
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
    default ObjectReader<T> thenString(BiConsumer<? super T, ? super String> setter) {
        return then(SectionReader.settingString(setter));
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
    default ObjectReader<T> thenIntArrayLine(BiConsumer<? super T, int[]> setter) {
        return then(SectionReader.settingChild(setter, LineReader.ofIntArray()));
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
    default ObjectReader<T> thenLongArrayLine(BiConsumer<? super T, long[]> setter) {
        return then(SectionReader.settingChild(setter, LineReader.ofLongArray()));
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
    default ObjectReader<T> thenDoubleArrayLine(BiConsumer<? super T, double[]> setter) {
        return then(SectionReader.settingChild(setter, LineReader.ofDoubleArray()));
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
    default ObjectReader<T> thenStringArrayLine(BiConsumer<? super T, String[]> setter) {
        return then(SectionReader.settingChild(setter, LineReader.ofStringArray()));
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
    default <E> ObjectReader<T> thenArrayLine(BiConsumer<? super T, ? super E[]> setter, IntFunction<E[]> arrayCreator,
            Function<? super String, ? extends E> itemConverter) {
        return then(SectionReader.settingChild(setter, LineReader.ofArray(arrayCreator, itemConverter)));
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
    default <E> ObjectReader<T> thenListLine(BiConsumer<? super T, ? super List<E>> setter,
            Function<String, ? extends E> itemConverter) {
        return then(SectionReader.settingChild(setter, LineReader.ofList(itemConverter)));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then creates an array of
     * objects from the next N lines, and sets it on the created object using the provided setter. N will be read at
     * parsing time from the current value of the given context variable, which needs to be previously set. A variable
     * can be set, for instance, using {@link #thenVar(String)} or {@link #thenVars(String...)}.
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
    default <E> ObjectReader<T> thenArray(BiConsumer<? super T, ? super E[]> setter, IntFunction<E[]> arrayCreator,
            String sizeVariable, ChildReader<? extends E, ? super T> itemReader) {
        return then(SectionReader.settingArray(setter, arrayCreator, (p, c) -> c.getVariableAsInt(sizeVariable),
                itemReader));
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
    default <E> ObjectReader<T> thenArray(BiConsumer<? super T, ? super E[]> setter, IntFunction<E[]> arrayCreator,
            Function<? super T, Integer> getSize, ChildReader<? extends E, ? super T> itemReader) {
        return then(SectionReader.settingArray(setter, arrayCreator, (p, c) -> getSize.apply(p), itemReader));
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
    default <E> ObjectReader<T> thenArray(BiConsumer<? super T, ? super E[]> setter, IntFunction<E[]> arrayCreator,
            BiFunction<? super T, Context, Integer> getSize, ChildReader<? extends E, ? super T> itemReader) {
        return then(SectionReader.settingArray(setter, arrayCreator, getSize, itemReader));
    }

    /**
     * Returns a new {@link ObjectReader} that creates the same object as this reader, and then creates a list of
     * objects from the next N lines, and sets it on the created object using the provided setter. N will be read at
     * parsing time from the current value of the given context variable, which needs to be previously set. A variable
     * can be set, for instance, using {@link #thenFieldsAndVars(String...)}.
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
    default <E> ObjectReader<T> thenList(BiConsumer<? super T, ? super List<E>> setter, String sizeVariable,
            ChildReader<? extends E, ? super T> itemReader) {
        return then(SectionReader.settingList(setter, (p, c) -> c.getVariableAsInt(sizeVariable), itemReader));
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
    default <E> ObjectReader<T> thenList(BiConsumer<? super T, ? super List<E>> setter,
            Function<? super T, Integer> getSize, ChildReader<? extends E, ? super T> itemReader) {
        return then(SectionReader.settingList(setter, (p, c) -> getSize.apply(p), itemReader));
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
    default <E> ObjectReader<T> thenList(BiConsumer<? super T, ? super List<E>> setter,
            BiFunction<? super T, Context, Integer> getSize, ChildReader<? extends E, ? super T> itemReader) {
        return then(SectionReader.settingList(setter, getSize, itemReader));
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
    default <C> ObjectReader<T> thenChild(BiConsumer<? super T, ? super C> setter,
            ChildReader<C, ? super T> childReader) {
        return then(SectionReader.settingChild(setter, childReader));
    }

}
