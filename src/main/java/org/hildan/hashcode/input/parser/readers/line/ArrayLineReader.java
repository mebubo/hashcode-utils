package org.hildan.hashcode.input.parser.readers.line;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;

import org.hildan.hashcode.input.config.Config;
import org.hildan.hashcode.input.parser.conversion.TypeConversionException;

public class ArrayLineReader<E, P> extends SingleLineSectionReader<P> {

    private final BiConsumer<P, E[]> parentUpdater;

    private final Function<String, E> converter;

    private final IntFunction<E[]> arrayCreator;

    public ArrayLineReader(IntFunction<E[]> arrayCreator, Function<String, E> converter,
                           BiConsumer<P, E[]> parentUpdater) {
        this.parentUpdater = parentUpdater;
        this.converter = converter;
        this.arrayCreator = arrayCreator;
    }

    @Override
    protected void setValues(P objectToFill, String[] values, Config config) throws TypeConversionException {
        E[] array = Arrays.stream(values).map(converter).toArray(arrayCreator);
        parentUpdater.accept(objectToFill, array);
    }
}
