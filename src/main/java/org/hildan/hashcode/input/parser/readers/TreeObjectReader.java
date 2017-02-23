package org.hildan.hashcode.input.parser.readers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import org.hildan.hashcode.input.config.Config;
import org.hildan.hashcode.input.parser.Context;
import org.hildan.hashcode.input.parser.readers.line.ArrayLineReader;
import org.hildan.hashcode.input.parser.readers.line.DoubleArrayLineReader;
import org.hildan.hashcode.input.parser.readers.line.FieldsLineReader;
import org.hildan.hashcode.input.parser.readers.line.IntArrayLineReader;
import org.hildan.hashcode.input.parser.readers.line.ListLineReader;
import org.hildan.hashcode.input.parser.readers.line.LongArrayLineReader;
import org.hildan.hashcode.input.parser.readers.section.ArraySectionReader;
import org.hildan.hashcode.input.parser.readers.section.ListSectionReader;
import org.hildan.hashcode.input.parser.readers.section.ObjectSectionReader;
import org.hildan.hashcode.input.parser.readers.section.SectionReader;

public class TreeObjectReader<T> implements ObjectReader<T> {

    private final Supplier<T> constructor;

    private final List<SectionReader<T>> sectionReaders;

    private TreeObjectReader(Supplier<T> constructor) {
        this.constructor = constructor;
        this.sectionReaders = new ArrayList<>();
    }

    public static <T> TreeObjectReader<T> of(Supplier<T> constructor) {
        return new TreeObjectReader<>(constructor);
    }

    public TreeObjectReader<T> addSectionReader(SectionReader<T> sectionReader) {
        sectionReaders.add(sectionReader);
        return this;
    }

    public T read(Context context, Config config) {
        T obj = constructor.get();
        for (SectionReader<T> sectionReader : sectionReaders) {
            sectionReader.readSection(obj, context, config);
        }
        return obj;
    }

    public TreeObjectReader<T> addFieldsLine(String... fieldNames) {
        return addSectionReader(new FieldsLineReader<>(fieldNames));
    }

    public TreeObjectReader<T> addIntArrayLine(BiConsumer<T, int[]> setter) {
        return addSectionReader(new IntArrayLineReader<>(setter));
    }

    public TreeObjectReader<T> addLongArrayLine(BiConsumer<T, long[]> setter) {
        return addSectionReader(new LongArrayLineReader<>(setter));
    }

    public TreeObjectReader<T> addDoubleArrayLine(BiConsumer<T, double[]> setter) {
        return addSectionReader(new DoubleArrayLineReader<>(setter));
    }

    public <E> TreeObjectReader<T> addArrayLine(IntFunction<E[]> arrayCreator, Function<String, E> itemConverter,
                                                BiConsumer<T, E[]> setter) {
        return addSectionReader(new ArrayLineReader<>(arrayCreator, itemConverter, setter));
    }

    public <E> TreeObjectReader<T> addListLine(Function<String, E> itemConverter, BiConsumer<T, List<E>> setter) {
        return addSectionReader(new ListLineReader<>(itemConverter, setter));
    }

    public <E> TreeObjectReader<T> addArray(IntFunction<E[]> arrayCreator, TreeObjectReader<E> itemCreator,
                                            Function<T, Integer> getCount, BiConsumer<T, E[]> setter) {
        return addSectionReader(new ArraySectionReader<>(arrayCreator, itemCreator, getCount, setter));
    }

    public <E> TreeObjectReader<T> addList(TreeObjectReader<E> itemCreator, Function<T, Integer> getCount,
                                           BiConsumer<T, List<E>> setter) {
        return addSectionReader(new ListSectionReader<>(itemCreator, getCount, setter));
    }

    public <C> TreeObjectReader<T> addObject(ObjectReader<C> childReader, BiConsumer<T, C> setter) {
        return addSectionReader(new ObjectSectionReader<>(childReader, setter));
    }
}
