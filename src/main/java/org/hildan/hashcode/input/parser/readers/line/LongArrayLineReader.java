package org.hildan.hashcode.input.parser.readers.line;

import java.util.Arrays;
import java.util.function.BiConsumer;

import org.hildan.hashcode.input.config.Config;

public class LongArrayLineReader<P> extends SingleLineSectionReader<P> {

    private final BiConsumer<P, long[]> parentUpdater;

    public LongArrayLineReader(BiConsumer<P, long[]> parentUpdater) {
        this.parentUpdater = parentUpdater;
    }

    @Override
    protected void setValues(P objectToFill, String[] values, Config config) {
        long[] array = Arrays.stream(values).mapToLong(Long::parseLong).toArray();
        parentUpdater.accept(objectToFill, array);
    }
}
