package org.hildan.hashcode.utils.parser.readers.line;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.context.Context;
import org.hildan.hashcode.utils.parser.readers.ObjectReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LineReader<T> implements ObjectReader<T, Object> {

    private final Function<? super String[], T> converter;

    public LineReader(Function<? super String[], T> converter) {
        this.converter = converter;
    }

    @NotNull
    @Override
    public T read(@NotNull Context context, @Nullable Object parent) throws InputParsingException {
        int lineNum = context.getNextLineNumber();
        String[] values = context.readArrayLine();
        try {
            return converter.apply(values);
        } catch (Exception e) {
            throw new InputParsingException(lineNum, String.join(" ", values), e.getMessage(), e);
        }
    }

    public static LineReader<int[]> ints() {
        return new LineReader<>(arr -> Arrays.stream(arr).mapToInt(Integer::parseInt).toArray());
    }

    public static LineReader<long[]> longs() {
        return new LineReader<>(arr -> Arrays.stream(arr).mapToLong(Long::parseLong).toArray());
    }

    public static LineReader<double[]> doubles() {
        return new LineReader<>(arr -> Arrays.stream(arr).mapToDouble(Double::parseDouble).toArray());
    }

    public static LineReader<String[]> strings() {
        return new LineReader<>(arr -> arr);
    }

    public static <E> LineReader<E[]> array(IntFunction<E[]> arrayCreator,
                                            Function<? super String, ? extends E> converter) {
        return new LineReader<>(arr -> Arrays.stream(arr).map(converter).toArray(arrayCreator));
    }

    public static <E> LineReader<List<E>> list(Function<? super String, ? extends E> converter) {
        return new LineReader<>(arr -> Arrays.stream(arr).map(converter).collect(Collectors.toList()));
    }

    public static <E, C extends Collection<E>> LineReader<C> collection(Supplier<C> constructor,
                                                                        Function<? super String, ? extends E>
                                                                                converter) {
        return new LineReader<>(arr -> Arrays.stream(arr).map(converter).collect(Collectors.toCollection(constructor)));
    }
}
