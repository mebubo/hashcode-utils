package org.hildan.hashcode.input.parser.readers.line;

import java.util.Arrays;
import java.util.function.BiConsumer;

import org.hildan.hashcode.input.config.Config;
import org.hildan.hashcode.input.parser.Context;

public class DoubleArrayLineReader<P> extends SingleLineSectionReader<P> {

    private final BiConsumer<P, double[]> parentSetter;

    public DoubleArrayLineReader(BiConsumer<P, double[]> parentSetter) {
        this.parentSetter = parentSetter;
    }

    @Override
    protected void setValues(P objectToFill, String[] values, Context context, Config config) {
        double[] array = Arrays.stream(values).mapToDouble(Double::parseDouble).toArray();
        parentSetter.accept(objectToFill, array);
    }
}
