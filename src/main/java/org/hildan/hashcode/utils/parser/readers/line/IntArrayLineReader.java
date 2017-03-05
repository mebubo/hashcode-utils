package org.hildan.hashcode.utils.parser.readers.line;

import java.util.Arrays;
import java.util.function.BiConsumer;

import org.hildan.hashcode.utils.parser.config.Config;
import org.hildan.hashcode.utils.parser.Context;

public class IntArrayLineReader<P> extends SingleLineSectionReader<P> {

    private final BiConsumer<P, int[]> parentSetter;

    public IntArrayLineReader(BiConsumer<P, int[]> parentSetter) {
        this.parentSetter = parentSetter;
    }

    @Override
    protected void setValues(P objectToFill, String[] values, Context context, Config config) {
        int[] array = Arrays.stream(values).mapToInt(Integer::parseInt).toArray();
        parentSetter.accept(objectToFill, array);
    }
}
