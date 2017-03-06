package org.hildan.hashcode.utils.parser.readers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import org.hildan.hashcode.utils.parser.Context;
import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.config.Config;
import org.hildan.hashcode.utils.parser.readers.line.ArrayLineReader;
import org.hildan.hashcode.utils.parser.readers.line.DoubleArrayLineReader;
import org.hildan.hashcode.utils.parser.readers.line.FieldsLineReader;
import org.hildan.hashcode.utils.parser.readers.line.IntArrayLineReader;
import org.hildan.hashcode.utils.parser.readers.line.ListLineReader;
import org.hildan.hashcode.utils.parser.readers.line.LongArrayLineReader;
import org.hildan.hashcode.utils.parser.readers.section.ObjectSectionReader;
import org.hildan.hashcode.utils.parser.readers.section.SectionReader;
import org.hildan.hashcode.utils.parser.readers.section.collection.ArraySectionReader;
import org.hildan.hashcode.utils.parser.readers.section.collection.ListSectionReader;

/**
 * An implementation of {@link ObjectReader} that builds an object using child {@link SectionReader}s.
 * <p>
 * The object is first created, then all section readers are sequentially called (respecting the order) to consume as
 * much input as necessary to fill up the created object.
 *
 * @param <T>
 *         the type of object this {@code TreeObjectReader} creates
 *
 * @see SectionReader
 */
public class TreeObjectReader<T> implements ObjectReader<T> {

    private final Supplier<T> constructor;

    private final List<SectionReader<T>> sectionReaders;

    private TreeObjectReader(Supplier<T> constructor) {
        this.constructor = constructor;
        this.sectionReaders = new ArrayList<>();
    }

    /**
     * Creates a new {@code TreeObjectReader} for the given type.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param <T>
     *         the type of objects that the new {@code TreeObjectReader} should create
     *
     * @return a new {@code TreeObjectReader}
     */
    public static <T> TreeObjectReader<T> of(Supplier<T> constructor) {
        return new TreeObjectReader<>(constructor);
    }

    /**
     * Adds the given {@link SectionReader} to the children of this reader. The order matters, because each child
     * reader will be called in the order of insertion.
     * <p>
     * This is the most generic configuration method of {@code TreeObjectReader}, and most of the time you should use
     * the other configuration methods instead.
     *
     * @param sectionReader
     *         the section reader to use to read a part of the created object
     *
     * @return this {@code TreeObjectReader}, for a convenient configuration syntax
     */
    public TreeObjectReader<T> addSectionReader(SectionReader<T> sectionReader) {
        sectionReaders.add(sectionReader);
        return this;
    }

    @Override
    public T read(Context context, Config config) throws InputParsingException {
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

    public TreeObjectReader<T> addStringArrayLine(BiConsumer<T, String[]> setter) {
        return addSectionReader(new ArrayLineReader<>(String[]::new, s -> s, setter));
    }

    public <E> TreeObjectReader<T> addArrayLine(BiConsumer<T, E[]> setter, IntFunction<E[]> arrayCreator,
            Function<String, E> itemConverter) {
        return addSectionReader(new ArrayLineReader<>(arrayCreator, itemConverter, setter));
    }

    public <E> TreeObjectReader<T> addListLine(BiConsumer<T, List<E>> setter, Function<String, E> itemConverter) {
        return addSectionReader(new ListLineReader<>(itemConverter, setter));
    }

    public <E> TreeObjectReader<T> addArray(BiConsumer<T, E[]> setter, IntFunction<E[]> arrayCreator,
            String sizeVariable, ObjectReader<E> itemReader) {
        return addSectionReader(new ArraySectionReader<>(arrayCreator, itemReader, sizeVariable, setter));
    }

    public <E> TreeObjectReader<T> addArray(BiConsumer<T, E[]> setter, IntFunction<E[]> arrayCreator,
            Function<T, Integer> getCount, ObjectReader<E> itemReader) {
        return addSectionReader(new ArraySectionReader<>(arrayCreator, itemReader, getCount, setter));
    }

    public <E> TreeObjectReader<T> addList(BiConsumer<T, List<E>> setter, String sizeVariable, ObjectReader<E> itemReader) {
        return addSectionReader(new ListSectionReader<>(itemReader, sizeVariable, setter));
    }

    public <E> TreeObjectReader<T> addList(BiConsumer<T, List<E>> setter, Function<T, Integer> getCount,
            ObjectReader<E> itemReader) {
        return addSectionReader(new ListSectionReader<>(itemReader, getCount, setter));
    }

    public <C> TreeObjectReader<T> addObject(BiConsumer<T, C> setter, ObjectReader<C> childReader) {
        return addSectionReader(new ObjectSectionReader<>(childReader, setter));
    }
}
