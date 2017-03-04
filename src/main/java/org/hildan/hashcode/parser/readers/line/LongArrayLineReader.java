package org.hildan.hashcode.parser.readers.line;

import java.util.Arrays;
import java.util.function.BiConsumer;

import org.hildan.hashcode.parser.config.Config;
import org.hildan.hashcode.parser.Context;

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
