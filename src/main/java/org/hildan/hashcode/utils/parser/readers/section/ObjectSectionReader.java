package org.hildan.hashcode.utils.parser.readers.section;

import java.util.function.BiConsumer;

import org.hildan.hashcode.utils.parser.config.Config;
import org.hildan.hashcode.utils.parser.Context;
import org.hildan.hashcode.utils.parser.readers.ObjectReader;

public class ObjectSectionReader<T, P> extends BaseSectionReader<T, P> {

    private final ObjectReader<T> childReader;

    public ObjectSectionReader(ObjectReader<T> childReader, BiConsumer<P, T> parentSetter) {
        super(parentSetter);
        this.childReader = childReader;
    }

    @Override
    protected T readSectionValue(P objectToFill, Context context, Config config) {
        return childReader.read(context, config);
    }
}
