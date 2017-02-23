package org.hildan.hashcode.input.parser.readers.section;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.hildan.hashcode.input.InputParsingException;
import org.hildan.hashcode.input.config.Config;
import org.hildan.hashcode.input.parser.Context;
import org.hildan.hashcode.input.parser.readers.TreeObjectReader;

public class ListSectionReader<E, P> extends BaseSectionReader<List<E>, P> {

    private final TreeObjectReader<E> itemCreator;

    private final Function<P, Integer> getCount;

    public ListSectionReader(TreeObjectReader<E> itemCreator, Function<P, Integer> getCount,
                             BiConsumer<P, List<E>> parentUpdater) {
        super(parentUpdater);
        this.getCount = getCount;
        this.itemCreator = itemCreator;
    }

    @Override
    public List<E> readSectionValue(P objectToFill, Context context, Config config) throws InputParsingException {
        int count = getCount.apply(objectToFill);
        List<E> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(itemCreator.read(context, config));
        }
        return list;
    }
}
