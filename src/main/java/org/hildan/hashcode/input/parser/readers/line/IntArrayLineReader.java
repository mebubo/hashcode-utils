package org.hildan.hashcode.input.parser.readers.line;

import java.util.Arrays;
import java.util.function.BiConsumer;

import org.hildan.hashcode.input.config.Config;

public class IntArrayLineReader<P> extends SingleLineSectionReader<P> {

    private final BiConsumer<P, int[]> parentUpdater;

    public IntArrayLineReader(BiConsumer<P, int[]> parentUpdater) {
        this.parentUpdater = parentUpdater;
    }

    @Override
    protected void setValues(P objectToFill, String[] values, Config config) {
        int[] array = Arrays.stream(values).mapToInt(Integer::parseInt).toArray();
        parentUpdater.accept(objectToFill, array);
    }
}
