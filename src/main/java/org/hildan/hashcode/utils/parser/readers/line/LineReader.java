package org.hildan.hashcode.utils.parser.readers.line;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.context.Context;
import org.hildan.hashcode.utils.parser.readers.ChildReader;
import org.hildan.hashcode.utils.parser.readers.ObjectReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link ObjectReader} that reads a full line of input to create an object.
 *
 * @param <T>
 *         the type of objects created by this {@link LineReader}
 * @param <P>
 *         the type of parent that the read objects are part of
 */
public class LineReader<T, P> implements ChildReader<T, P> {

    private final BiFunction<? super String[], P, T> lineConverter;

    /**
     * Creates a new {@link LineReader}.
     *
     * @param lineConverter
     *         the function to convert the input line's tokens into an object
     */
    @SuppressWarnings("WeakerAccess")
    protected LineReader(BiFunction<? super String[], P, T> lineConverter) {
        this.lineConverter = lineConverter;
    }

    @NotNull
    @Override
    @SuppressWarnings("checkstyle:illegalcatch")
    public T read(@NotNull Context context, @Nullable P parent) throws InputParsingException {
        String[] line = context.readLine();
        try {
            return lineConverter.apply(line, parent);
        } catch (Exception e) {
            throw context.wrapException("exception while converting the values: " + Arrays.toString(line), e);
        }
    }

    /**
     * Creates a new {@link ObjectReader} that reads a full line as an array of strings.
     *
     * @return the created {@link ObjectReader}
     */
    public static ObjectReader<String[]> ofStringArray() {
        return Context::readLine;
    }

    /**
     * Creates a new {@link ObjectReader} that reads a full line as an array of integers.
     *
     * @return the created {@link ObjectReader}
     */
    public static ObjectReader<int[]> ofIntArray() {
        return ofObject(arr -> Arrays.stream(arr).mapToInt(Integer::parseInt).toArray());
    }

    /**
     * Creates a new {@link ObjectReader} that reads a full line as an array of long.
     *
     * @return the created {@link ObjectReader}
     */
    public static ObjectReader<long[]> ofLongArray() {
        return ofObject(arr -> Arrays.stream(arr).mapToLong(Long::parseLong).toArray());
    }

    /**
     * Creates a new {@link ObjectReader} that reads a full line as an array of doubles.
     *
     * @return the created {@link ObjectReader}
     */
    public static ObjectReader<double[]> ofDoubleArray() {
        return ofObject(arr -> Arrays.stream(arr).mapToDouble(Double::parseDouble).toArray());
    }

    /**
     * Creates a new {@link ObjectReader} that reads a full line as an array of objects.
     *
     * @param arrayCreator
     *         a function to create a new array, given its size
     * @param itemConverter
     *         a function to convert a string token into an item of the array
     * @param <E>
     *         the type of elements in the array
     *
     * @return the created {@link ObjectReader}
     */
    public static <E> ObjectReader<E[]> ofArray(IntFunction<E[]> arrayCreator,
            Function<? super String, ? extends E> itemConverter) {
        return ofObject(arr -> Arrays.stream(arr).map(itemConverter).toArray(arrayCreator));
    }

    /**
     * Creates a new {@link ObjectReader} that reads a full line as a list of objects.
     *
     * @param itemConverter
     *         a function to convert a string token into an item of the list
     * @param <E>
     *         the type of elements in the list
     *
     * @return the created {@link ObjectReader}
     */
    public static <E> ObjectReader<List<E>> ofList(Function<? super String, ? extends E> itemConverter) {
        return ofCollection(itemConverter, Collectors.toList());
    }

    /**
     * Creates a new {@link ObjectReader} that reads a full line of tokens and reduces these tokens using the provided
     * collector.
     *
     * @param itemConverter
     *         a function to convert a string token into an item of the list
     * @param collector
     *         a collector describing the reduction to perform
     * @param <E>
     *         the type of elements in the list
     * @param <R>
     *         the type of the result of the reduction, for instance a collection type
     *
     * @return the created {@link ObjectReader}
     */
    public static <E, R> ObjectReader<R> ofCollection(Function<? super String, ? extends E> itemConverter,
            Collector<E, ?, R> collector) {
        return ofObject(arr -> Arrays.stream(arr).map(itemConverter).collect(collector));
    }

    /**
     * Creates a new {@link ObjectReader} that reads a full line and converts the result using the given converter.
     *
     * @param converter
     *         the function to convert the input line's tokens into an object
     * @param <T>
     *         the type of objects created by the returned {@link ObjectReader}
     *
     * @return the created {@link ObjectReader}
     */
    public static <T> ObjectReader<T> ofObject(Function<? super String[], T> converter) {
        LineReader<T, Object> reader = new LineReader<>((String[] line, Object parent) -> converter.apply(line));
        return ctx -> reader.read(ctx, null);
    }

    /**
     * Creates a new {@link ChildReader} that reads a full line and converts the result using the given converter. As
     * opposed to {@link #ofObject(Function)}, the converter is also passed a parent object within which the reader will
     * be called. This is why this method returns a {@link ChildReader} and not an {@link ObjectReader}.
     *
     * @param converter
     *         the function to convert the input line's tokens into an object. The converter is also given the parent
     *         object within which the reader will be called.
     * @param <T>
     *         the type of objects created by the returned {@link ObjectReader}
     * @param <P>
     *         the type of parent that the read objects are part of
     *
     * @return the created {@link ChildReader}
     */
    public static <T, P> ChildReader<T, P> ofChild(BiFunction<? super String[], P, T> converter) {
        return new LineReader<>(converter);
    }
}
