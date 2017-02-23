package org.hildan.hashcode.input.parser.readers.section;

import java.util.function.BiConsumer;

import org.hildan.hashcode.input.InputParsingException;
import org.hildan.hashcode.input.config.Config;
import org.hildan.hashcode.input.parser.Context;

public abstract class BaseSectionReader<T, P> implements SectionReader<P> {

    private final BiConsumer<P, T> parentUpdater;

    public BaseSectionReader(BiConsumer<P, T> parentUpdater) {
        this.parentUpdater = parentUpdater;
    }

    @Override
    public void readSection(P objectToFill, Context context, Config config) throws InputParsingException {
        T value = readSectionValue(objectToFill, context, config);
        parentUpdater.accept(objectToFill, value);
    }

    protected abstract T readSectionValue(P objectToFill, Context context, Config config) throws InputParsingException;
}
