package org.hildan.hashcode.input.parser.readers.section;

import java.util.function.BiConsumer;

import org.hildan.hashcode.input.config.Config;
import org.hildan.hashcode.input.parser.Context;
import org.hildan.hashcode.input.parser.readers.ObjectReader;

public class ObjectSectionReader<T, P> extends BaseSectionReader<T, P> {

    private final ObjectReader<T> childReader;

    public ObjectSectionReader(ObjectReader<T> childReader, BiConsumer<P, T> parentUpdater) {
        super(parentUpdater);
        this.childReader = childReader;
    }

    @Override
    protected T readSectionValue(P objectToFill, Context context, Config config) {
        return childReader.read(context, config);
    }
}
