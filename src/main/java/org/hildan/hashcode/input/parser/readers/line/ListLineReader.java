package org.hildan.hashcode.input.parser.readers.line;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hildan.hashcode.input.config.Config;
import org.hildan.hashcode.input.parser.conversion.TypeConversionException;

public class ListLineReader<E, P> extends SingleLineSectionReader<P> {

    private final BiConsumer<P, List<E>> parentUpdater;

    private final Function<String, E> converter;

    public ListLineReader(Function<String, E> converter, BiConsumer<P, List<E>> parentUpdater) {
        this.parentUpdater = parentUpdater;
        this.converter = converter;
    }

    @Override
    protected void setValues(P objectToFill, String[] values, Config config) throws TypeConversionException {
        List<E> list = Arrays.stream(values).map(converter).collect(Collectors.toList());
        parentUpdater.accept(objectToFill, list);
    }
}
