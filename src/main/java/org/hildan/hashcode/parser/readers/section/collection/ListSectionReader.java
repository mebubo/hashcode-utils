package org.hildan.hashcode.parser.readers.section.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.hildan.hashcode.parser.readers.TreeObjectReader;

public class ListSectionReader<E, P> extends ContainerSectionReader<E, List<E>, P> {

    public ListSectionReader(TreeObjectReader<E> itemReader, Function<P, Integer> getSize,
            BiConsumer<P, List<E>> parentSetter) {
        super(ArrayList::new, itemReader, getSize, parentSetter);
    }

    public ListSectionReader(TreeObjectReader<E> itemReader, String sizeVariable, BiConsumer<P, List<E>> parentSetter) {
        super(ArrayList::new, itemReader, sizeVariable, parentSetter);
    }

    @Override
    protected void add(List<E> collection, int index, E element) {
        collection.add(index, element);
    }
}
