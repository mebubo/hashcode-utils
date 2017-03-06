package org.hildan.hashcode.utils.parser.readers.section.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.hildan.hashcode.utils.parser.readers.ObjectReader;
import org.hildan.hashcode.utils.parser.readers.section.SectionReader;

/**
 * A {@link SectionReader} that creates a list, reads multiple child objects from the input, adding them to the list,
 * and sets the created list on the parent object.
 *
 * @param <E>
 *         the type of elements in the list
 * @param <P>
 *         the type of parent on which this {@code ArraySectionReader} sets the created list
 */
public class ListSectionReader<E, P> extends ContainerSectionReader<E, List<E>, P> {

    public ListSectionReader(ObjectReader<E> itemReader, Function<P, Integer> getSize,
                             BiConsumer<P, List<E>> parentSetter) {
        super(ArrayList::new, itemReader, getSize, parentSetter);
    }

    public ListSectionReader(ObjectReader<E> itemReader, String sizeVariable, BiConsumer<P, List<E>> parentSetter) {
        super(ArrayList::new, itemReader, sizeVariable, parentSetter);
    }

    @Override
    protected void add(List<E> container, int index, E element) {
        container.add(index, element);
    }
}
