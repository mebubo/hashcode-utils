package org.hildan.hashcode.input.parser.readers.line;

import java.util.Arrays;
import java.util.function.BiConsumer;

import org.hildan.hashcode.input.config.Config;
import org.hildan.hashcode.input.parser.conversion.TypeConversionException;

public class DoubleArrayLineReader<P> extends SingleLineSectionReader<P> {

    private final BiConsumer<P, double[]> parentUpdater;

    public DoubleArrayLineReader(BiConsumer<P, double[]> parentUpdater) {
        this.parentUpdater = parentUpdater;
    }

    @Override
    protected void setValues(P objectToFill, String[] values, Config config) throws TypeConversionException {
        double[] array = Arrays.stream(values).mapToDouble(Double::parseDouble).toArray();
        parentUpdater.accept(objectToFill, array);
    }
}
