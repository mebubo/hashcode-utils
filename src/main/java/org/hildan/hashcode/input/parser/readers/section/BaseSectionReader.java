package org.hildan.hashcode.input.parser.readers.section;

import java.util.function.BiConsumer;

import org.hildan.hashcode.input.InputParsingException;
import org.hildan.hashcode.input.config.Config;
import org.hildan.hashcode.input.parser.Context;

public abstract class BaseSectionReader<T, P> implements SectionReader<P> {

    private final BiConsumer<P, T> parentSetter;

    public BaseSectionReader(BiConsumer<P, T> parentSetter) {
        this.parentSetter = parentSetter;
    }

    @Override
    public void readSection(P objectToFill, Context context, Config config) throws InputParsingException {
        T value = readSectionValue(objectToFill, context, config);
        parentSetter.accept(objectToFill, value);
    }

    protected abstract T readSectionValue(P objectToFill, Context context, Config config) throws InputParsingException;
}
