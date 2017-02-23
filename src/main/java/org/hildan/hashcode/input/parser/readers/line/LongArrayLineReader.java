package org.hildan.hashcode.input.parser.readers.line;

import java.util.Arrays;
import java.util.function.BiConsumer;

import org.hildan.hashcode.input.config.Config;
import org.hildan.hashcode.input.parser.Context;

public class LongArrayLineReader<P> extends SingleLineSectionReader<P> {

    private final BiConsumer<P, long[]> parentSetter;

    public LongArrayLineReader(BiConsumer<P, long[]> parentSetter) {
        this.parentSetter = parentSetter;
    }

    @Override
    protected void setValues(P objectToFill, String[] values, Context context, Config config) {
        long[] array = Arrays.stream(values).mapToLong(Long::parseLong).toArray();
        parentSetter.accept(objectToFill, array);
    }
}
