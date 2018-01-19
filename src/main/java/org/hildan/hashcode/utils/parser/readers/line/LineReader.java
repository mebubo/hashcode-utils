package org.hildan.hashcode.utils.parser.readers.line;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.context.Context;
import org.hildan.hashcode.utils.parser.readers.ChildReader;
import org.hildan.hashcode.utils.parser.readers.ObjectReader;
import org.jetbrains.annotations.NotNull;

/**
 * An {@link ChildReader} that reads a full line of input to create an object, with no side-effect on the parent.
 *
 * @param <T>
 *         the type of objects created by this {@code LineReader}
 */
public class LineReader<T> implements ObjectReader<T> {

    private final Function<? super String[], T> converter;

    /**
     * Creates a new {@link LineReader}.
     *
     * @param converter
     *         the function to convert the input line's tokens into an object
     */
    public LineReader(Function<? super String[], T> converter) {
        this.converter = converter;
    }

    @NotNull
    @Override
    public T read(@NotNull Context context) throws InputParsingException {
        String[] values = context.readLine();
        try {
            return converter.apply(values);
        } catch (Exception e) {
            throw context.wrapException(e);
        }
    }

    /**
     * Creates a new {@code LineReader} that reads a full line as an array of integers.
     *
     * @return the created {@code LineReader}
     */
    public static LineReader<int[]> ints() {
        return new LineReader<>(arr -> Arrays.stream(arr).mapToInt(Integer::parseInt).toArray());
    }

    /**
     * Creates a new {@code LineReader} that reads a full line as an array of long.
     *
     * @return the created {@code LineReader}
     */
    public static LineReader<long[]> longs() {
        return new LineReader<>(arr -> Arrays.stream(arr).mapToLong(Long::parseLong).toArray());
    }

    /**
     * Creates a new {@code LineReader} that reads a full line as an array of doubles.
     *
     * @return the created {@code LineReader}
     */
    public static LineReader<double[]> doubles() {
        return new LineReader<>(arr -> Arrays.stream(arr).mapToDouble(Double::parseDouble).toArray());
    }

    /**
     * Creates a new {@code LineReader} that reads a full line as an array of strings.
     *
     * @return the created {@code LineReader}
     */
    public static LineReader<String[]> strings() {
        return new LineReader<>(arr -> arr);
    }

    /**
     * Creates a new {@code LineReader} that reads a full line as an array of objects.
     *
     * @param arrayCreator
     *         a function to create a new array, given its size
     * @param itemConverter
     *         a function to convert a string token into an item of the array
     * @param <E>
     *         the type of elements in the array
     *
     * @return the created {@code LineReader}
     */
    public static <E> LineReader<E[]> array(IntFunction<E[]> arrayCreator,
                                            Function<? super String, ? extends E> itemConverter) {
        return new LineReader<>(arr -> Arrays.stream(arr).map(itemConverter).toArray(arrayCreator));
    }

    /**
     * Creates a new {@code LineReader} that reads a full line as a list of objects.
     *
     * @param itemConverter
     *         a function to convert a string token into an item of the list
     * @param <E>
     *         the type of elements in the list
     *
     * @return the created {@code LineReader}
     */
    public static <E> LineReader<List<E>> list(Function<? super String, ? extends E> itemConverter) {
        return collect(itemConverter, Collectors.toList());
    }

    /**
     * Creates a new {@code LineReader} that reads a full line of tokens and reduces these tokens using the provided
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
     * @return the created {@code LineReader}
     */
    public static <E, R> LineReader<R> collect(Function<? super String, ? extends E> itemConverter,
                                               Collector<E, ?, R> collector) {
        return new LineReader<>(arr -> Arrays.stream(arr).map(itemConverter).collect(collector));
    }
}
