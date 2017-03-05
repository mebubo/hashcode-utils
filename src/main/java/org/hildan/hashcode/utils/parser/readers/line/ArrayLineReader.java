package org.hildan.hashcode.utils.parser.readers.line;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;

import org.hildan.hashcode.utils.parser.config.Config;
import org.hildan.hashcode.utils.parser.Context;

public class ArrayLineReader<E, P> extends SingleLineSectionReader<P> {

    private final BiConsumer<P, E[]> parentSetter;

    private final Function<String, E> converter;

    private final IntFunction<E[]> arrayCreator;

    public ArrayLineReader(IntFunction<E[]> arrayCreator, Function<String, E> converter,
                           BiConsumer<P, E[]> parentSetter) {
        this.parentSetter = parentSetter;
        this.converter = converter;
        this.arrayCreator = arrayCreator;
    }

    @Override
    protected void setValues(P objectToFill, String[] values, Context context, Config config) {
        E[] array = Arrays.stream(values).map(converter).toArray(arrayCreator);
        parentSetter.accept(objectToFill, array);
    }
}
