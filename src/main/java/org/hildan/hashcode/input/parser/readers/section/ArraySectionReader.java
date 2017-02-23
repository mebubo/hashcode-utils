package org.hildan.hashcode.input.parser.readers.section;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;

import org.hildan.hashcode.input.InputParsingException;
import org.hildan.hashcode.input.config.Config;
import org.hildan.hashcode.input.parser.Context;
import org.hildan.hashcode.input.parser.readers.TreeObjectReader;

public class ArraySectionReader<E, P> extends BaseSectionReader<E[], P> {

    private final IntFunction<E[]> arrayCreator;

    private final TreeObjectReader<E> itemCreator;

    private final Function<P, Integer> getCount;

    public ArraySectionReader(IntFunction<E[]> arrayCreator, TreeObjectReader<E> itemCreator, Function<P, Integer> getCount,
                              BiConsumer<P, E[]> parentSetter) {
        super(parentSetter);
        this.arrayCreator = arrayCreator;
        this.getCount = getCount;
        this.itemCreator = itemCreator;
    }

    @Override
    protected E[] readSectionValue(P objectToFill, Context context, Config config) throws InputParsingException {
        int count = getCount.apply(objectToFill);
        E[] array = arrayCreator.apply(count);
        for (int i = 0; i < count; i++) {
            array[i] = itemCreator.read(context, config);
        }
        return array;
    }
}
