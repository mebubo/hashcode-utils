package org.hildan.hashcode.utils.parser.readers.section.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.hildan.hashcode.utils.parser.readers.ObjectReader;

public class ListSectionReader<E, P> extends ContainerSectionReader<E, List<E>, P> {

    public ListSectionReader(ObjectReader<E> itemReader, Function<P, Integer> getSize,
                             BiConsumer<P, List<E>> parentSetter) {
        super(ArrayList::new, itemReader, getSize, parentSetter);
    }

    public ListSectionReader(ObjectReader<E> itemReader, String sizeVariable, BiConsumer<P, List<E>> parentSetter) {
        super(ArrayList::new, itemReader, sizeVariable, parentSetter);
    }

    @Override
    protected void add(List<E> collection, int index, E element) {
        collection.add(index, element);
    }
}
