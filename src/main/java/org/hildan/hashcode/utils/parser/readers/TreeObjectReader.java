package org.hildan.hashcode.utils.parser.readers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.context.Context;
import org.hildan.hashcode.utils.parser.readers.creators.Int3Creator;
import org.hildan.hashcode.utils.parser.readers.creators.Int4Creator;
import org.hildan.hashcode.utils.parser.readers.creators.Int5Creator;
import org.hildan.hashcode.utils.parser.readers.creators.ObjectCreator;
import org.hildan.hashcode.utils.parser.readers.line.ArrayLineReader;
import org.hildan.hashcode.utils.parser.readers.line.CollectionLineReader;
import org.hildan.hashcode.utils.parser.readers.line.DoubleArrayLineReader;
import org.hildan.hashcode.utils.parser.readers.line.FieldsAndVarsLineReader;
import org.hildan.hashcode.utils.parser.readers.line.IntArrayLineReader;
import org.hildan.hashcode.utils.parser.readers.line.LongArrayLineReader;
import org.hildan.hashcode.utils.parser.readers.section.ObjectSectionReader;
import org.hildan.hashcode.utils.parser.readers.section.SectionReader;
import org.hildan.hashcode.utils.parser.readers.section.collection.ArraySectionReader;
import org.hildan.hashcode.utils.parser.readers.section.collection.CollectionSectionReader;
import org.jetbrains.annotations.NotNull;

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

    private final ObjectCreator<T> constructor;

    private final List<SectionReader<T>> sectionReaders;

    private TreeObjectReader(ObjectCreator<T> constructor) {
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
        return new TreeObjectReader<>(ctx -> constructor.get());
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
    public static <T> TreeObjectReader<T> of(Function<Integer, T> constructor) {
        return new TreeObjectReader<>(ctx -> constructor.apply(ctx.readInt()));
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
    public static <T> TreeObjectReader<T> of(BiFunction<Integer, Integer, T> constructor) {
        return new TreeObjectReader<>(ctx -> constructor.apply(ctx.readInt(), ctx.readInt()));
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
    public static <T> TreeObjectReader<T> of(Int3Creator<T> constructor) {
        return new TreeObjectReader<>(constructor);
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
    public static <T> TreeObjectReader<T> of(Int4Creator<T> constructor) {
        return new TreeObjectReader<>(constructor);
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
    public static <T> TreeObjectReader<T> of(Int5Creator<T> constructor) {
        return new TreeObjectReader<>(constructor);
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
    public static <T> TreeObjectReader<T> of(ObjectCreator<T> constructor) {
        return new TreeObjectReader<>(constructor);
    }

    @Override
    public T read(@NotNull Context context) throws InputParsingException {
        T obj = constructor.create(context);
        for (SectionReader<T> sectionReader : sectionReaders) {
            sectionReader.readSection(obj, context);
        }
        return obj;
    }

    /**
     * Tells this reader to map each element of the next line to a field of the created object or to a context variable,
     * or both.
     * <p>
     * The field/variable names are given as strings that can each be one of: <ul> <li>a field name (e.g.
     * "myField1")</li> <li>a '@' symbol followed by a variable name (e.g. "@N", "@myVar", "@123"...)</li> <li>both a
     * field name and a variable name separated by a '@' (e.g. "nItems@N", "size@nbOfSatellites"...)</li> </ul>
     * <p>
     * Note that "" and "@" describe neither a field nor a variable, and thus the corresponding entry in the line will
     * be ignored during parsing. A null description is forbidden.
     *
     * @param fieldAndVarNames
     *         an array of field/variable names, as described above
     *
     * @return this {@code TreeObjectReader}, for a convenient configuration syntax
     */
    public TreeObjectReader<T> fieldsAndVarsLine(String... fieldAndVarNames) {
        return section(new FieldsAndVarsLineReader<>(fieldAndVarNames));
    }

    /**
     * Tells this reader to create an array of ints from the next line, and set it on the object being created using the
     * provided setter.
     *
     * @param setter
     *         the setter to call on the object being created, with the created array
     *
     * @return this {@code TreeObjectReader}, for a convenient configuration syntax
     */
    public TreeObjectReader<T> intArrayLine(BiConsumer<T, int[]> setter) {
        return section(new IntArrayLineReader<>(setter));
    }

    /**
     * Tells this reader to create an array of longs from the next line, and set it on the object being created using
     * the provided setter.
     *
     * @param setter
     *         the setter to call on the object being created, with the created array
     *
     * @return this {@code TreeObjectReader}, for a convenient configuration syntax
     */
    public TreeObjectReader<T> longArrayLine(BiConsumer<T, long[]> setter) {
        return section(new LongArrayLineReader<>(setter));
    }

    /**
     * Tells this reader to create an array of doubles from the next line, and set it on the object being created using
     * the provided setter.
     *
     * @param setter
     *         the setter to call on the object being created, with the created array
     *
     * @return this {@code TreeObjectReader}, for a convenient configuration syntax
     */
    public TreeObjectReader<T> doubleArrayLine(BiConsumer<T, double[]> setter) {
        return section(new DoubleArrayLineReader<>(setter));
    }

    /**
     * Tells this reader to create an array of strings from the next line, and set it on the object being created using
     * the provided setter.
     *
     * @param setter
     *         the setter to call on the object being created, with the created array
     *
     * @return this {@code TreeObjectReader}, for a convenient configuration syntax
     */
    public TreeObjectReader<T> stringArrayLine(BiConsumer<T, String[]> setter) {
        return section(new ArrayLineReader<>(setter, s -> s, String[]::new));
    }

    /**
     * Tells this reader to create an array of objects from the next line, and set it on the object being created using
     * the provided setter.
     *
     * @param setter
     *         the setter to call on the object being created, with the created array
     * @param arrayCreator
     *         a function to create a new array, given the desired size
     * @param itemConverter
     *         a function to convert each string element of the line into an element of the array
     * @param <E>
     *         the type of elements in the created array
     *
     *
     * @return this {@code TreeObjectReader}, for a convenient configuration syntax
     */
    public <E> TreeObjectReader<T> arrayLine(BiConsumer<T, E[]> setter, IntFunction<E[]> arrayCreator,
                                             Function<String, E> itemConverter) {
        return section(new ArrayLineReader<>(setter, itemConverter, arrayCreator));
    }

    /**
     * Tells this reader to create a list of objects from the next line, and set it on the object being created using
     * the provided setter.
     *
     * @param setter
     *         the setter to call on the object being created, with the created list
     * @param itemConverter
     *         a function to convert each string element of the line into an element of the list
     * @param <E>
     *         the type of elements in the created list
     *
     * @return this {@code TreeObjectReader}, for a convenient configuration syntax
     */
    public <E> TreeObjectReader<T> listLine(BiConsumer<T, List<E>> setter, Function<String, E> itemConverter) {
        return section(new CollectionLineReader<>(itemConverter, setter, ArrayList::new));
    }

    /**
     * Tells this reader to create an array of objects from the next N lines, and set it on the object being created
     * using the provided setter. N will be read at parsing time from the current value of the given context variable,
     * which needs to be previously set. A variable can be set, for instance, using {@link
     * #fieldsAndVarsLine(String...)}.
     *
     * @param setter
     *         the setter to call on the object being created, with the created array
     * @param arrayCreator
     *         a function to create a new array, given the desired size
     * @param sizeVariable
     *         a context variable to get
     * @param itemReader
     *         a child reader used to read each item
     * @param <E>
     *         the type of elements in the created array
     *
     * @return this {@code TreeObjectReader}, for a convenient configuration syntax
     */
    public <E> TreeObjectReader<T> arraySection(BiConsumer<T, E[]> setter, IntFunction<E[]> arrayCreator,
                                                String sizeVariable, ObjectReader<E> itemReader) {
        return section(new ArraySectionReader<>(arrayCreator, itemReader, sizeGetter(sizeVariable), setter));
    }

    /**
     * Tells this reader to create an array of objects from the next N lines, and set it on the object being created
     * using the provided setter. N will be read at parsing time by calling the provided getSize function.
     *
     * @param setter
     *         the setter to call on the object being created, with the created array
     * @param arrayCreator
     *         a function to create a new array, given the desired size
     * @param getSize
     *         a function to get the size of the array to create. It takes the object being created as parameter, as
     *         well as the current {@link Context}
     * @param itemReader
     *         a child reader used to read each item
     * @param <E>
     *         the type of elements in the created array
     *
     * @return this {@code TreeObjectReader}, for a convenient configuration syntax
     */
    public <E> TreeObjectReader<T> arraySection(BiConsumer<T, E[]> setter, IntFunction<E[]> arrayCreator,
                                                Function<T, Integer> getSize, ObjectReader<E> itemReader) {
        return section(new ArraySectionReader<>(arrayCreator, itemReader, sizeGetter(getSize), setter));
    }

    /**
     * Tells this reader to create an array of objects from the next N lines, and set it on the object being created
     * using the provided setter. N will be computed at parsing time by calling the provided getSize function.
     *
     * @param setter
     *         the setter to call on the object being created, with the created array
     * @param arrayCreator
     *         a function to create a new array, given the desired size
     * @param getSize
     *         a function to get the size of the list to create. It takes the object being created as parameter, as well
     *         as the current {@link Context}
     * @param itemReader
     *         a child reader used to read each item
     * @param <E>
     *         the type of elements in the created array
     *
     * @return this {@code TreeObjectReader}, for a convenient configuration syntax
     */
    public <E> TreeObjectReader<T> arraySection(BiConsumer<T, E[]> setter, IntFunction<E[]> arrayCreator,
                                                BiFunction<T, Context, Integer> getSize, ObjectReader<E> itemReader) {
        return section(new ArraySectionReader<>(arrayCreator, itemReader, getSize, setter));
    }

    /**
     * Tells this reader to create a list of objects from the next N lines, and set it on the object being created
     * using the provided setter. N will be read at parsing time from the current value of the given context variable,
     * which needs to be previously set. A variable can be set, for instance, using {@link
     * #fieldsAndVarsLine(String...)}.
     *
     * @param setter
     *         the setter to call on the object being created, with the created list
     * @param sizeVariable
     *         a context variable to get
     * @param itemReader
     *         a child reader used to read each item
     * @param <E>
     *         the type of elements in the created list
     *
     * @return this {@code TreeObjectReader}, for a convenient configuration syntax
     */
    public <E> TreeObjectReader<T> listSection(BiConsumer<T, List<E>> setter, String sizeVariable,
                                               ObjectReader<E> itemReader) {
        return section(new CollectionSectionReader<>(ArrayList::new, itemReader, sizeGetter(sizeVariable), setter));
    }

    /**
     * Tells this reader to create a list of objects from the next N lines, and set it on the object being created
     * using the provided setter. N will be read at parsing time by calling the provided getSize function.
     *
     * @param setter
     *         the setter to call on the object being created, with the created list
     * @param getSize
     *         a function to get the size of the array to create. It takes the object being created as parameter.
     * @param itemReader
     *         a child reader used to read each item
     * @param <E>
     *         the type of elements in the created list
     *
     * @return this {@code TreeObjectReader}, for a convenient configuration syntax
     */
    public <E> TreeObjectReader<T> listSection(BiConsumer<T, List<E>> setter, Function<T, Integer> getSize,
                                               ObjectReader<E> itemReader) {
        return section(new CollectionSectionReader<>(ArrayList::new, itemReader, sizeGetter(getSize), setter));
    }

    /**
     * Tells this reader to create a list of objects from the next N lines, and set it on the object being created
     * using the provided setter. N will be computed at parsing time by calling the provided getSize function.
     *
     * @param setter
     *         the setter to call on the object being created, with the created list
     * @param getSize
     *         a function to get the size of the array to create. It takes the object being created as parameter, as
     *         well as the current {@link Context}
     * @param itemReader
     *         a child reader used to read each item
     * @param <E>
     *         the type of elements in the created list
     *
     * @return this {@code TreeObjectReader}, for a convenient configuration syntax
     */
    public <E> TreeObjectReader<T> listSection(BiConsumer<T, List<E>> setter, BiFunction<T, Context, Integer> getSize,
                                               ObjectReader<E> itemReader) {
        return section(new CollectionSectionReader<>(ArrayList::new, itemReader, getSize, setter));
    }

    /**
     * Tells this reader to create an object from the next few lines using the given reader, and set it on the object
     * being created using the given setter. The childReader will read as many lines as necessary.
     *
     * @param setter
     *         the setter to call on the parent object being created, with the created child object
     * @param childReader
     *         a reader used to read the child object
     * @param <C>
     *         the type of object to create
     *
     * @return this {@code TreeObjectReader}, for a convenient configuration syntax
     */
    public <C> TreeObjectReader<T> objectSection(BiConsumer<T, C> setter, ObjectReader<C> childReader) {
        return section(new ObjectSectionReader<>(childReader, setter));
    }

    /**
     * Tells this reader to read the next few lines with the provided {@link SectionReader}. The given {@link
     * SectionReader} will read as many lines as necessary.
     * <p>
     * The order matters, because each child section reader will be called in the order of insertion.
     * <p>
     * This is the most generic configuration method of {@code TreeObjectReader}, and most of the time you should use
     * the other configuration methods instead.
     *
     * @param sectionReader
     *         the section reader to use to read a part of the created object
     *
     * @return this {@code TreeObjectReader}, for a convenient configuration syntax
     */
    public TreeObjectReader<T> section(SectionReader<T> sectionReader) {
        sectionReaders.add(sectionReader);
        return this;
    }

    private static <P> BiFunction<P, Context, Integer> sizeGetter(String sizeVariable) {
        return (p, c) -> c.getVariableAsInt(sizeVariable);
    }

    private static <P> BiFunction<P, Context, Integer> sizeGetter(Function<P, Integer> getSizeFromParent) {
        return (p, c) -> getSizeFromParent.apply(p);
    }
}
