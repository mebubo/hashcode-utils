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
import org.hildan.hashcode.utils.parser.readers.builder.ReaderBuilder;
import org.hildan.hashcode.utils.parser.readers.builder.StateReader;
import org.hildan.hashcode.utils.parser.readers.builder.VariableReader;
import org.hildan.hashcode.utils.parser.readers.creators.Int3Creator;
import org.hildan.hashcode.utils.parser.readers.creators.Int4Creator;
import org.hildan.hashcode.utils.parser.readers.creators.Int5Creator;
import org.hildan.hashcode.utils.parser.readers.creators.Int6Creator;
import org.hildan.hashcode.utils.parser.readers.creators.Int7Creator;
import org.hildan.hashcode.utils.parser.readers.creators.ObjectCreator;
import org.hildan.hashcode.utils.parser.readers.line.LineReader;
import org.hildan.hashcode.utils.parser.readers.section.FieldAndVarReader;
import org.hildan.hashcode.utils.parser.readers.section.FieldsAndVarsLineReader;
import org.hildan.hashcode.utils.parser.readers.section.ObjectSectionReader;
import org.hildan.hashcode.utils.parser.readers.section.SectionReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A default implementation of {@link ObjectReader}. It may use one or more {@link SectionReader}s to update the
 * fields of the created object.
 * <p>
 * The object is first created, then all section readers are sequentially called (respecting the order) to consume as
 * much input as necessary to fill up the created object.
 *
 * @param <T>
 *         the type of object this {@code RootReader} creates
 *
 * @see SectionReader
 */
public class RootReader<T> implements ObjectReader<T> {

    private final List<StateReader> preReaders;

    private final ObjectCreator<? extends T> constructor;

    private final List<SectionReader<? super T>> sectionReaders;

    /**
     * Creates a new {@code RootReader} that uses the given constructor to create objects.
     *
     * @param constructor
     *         the constructor to use to create new instances
     */
    public RootReader(ObjectCreator<? extends T> constructor) {
        this(new ArrayList<>(), constructor);
    }

    /**
     * Creates a new {@code RootReader} that executes the given pre-readers before calling the given constructor to
     * create the object. This is useful when setting some context variables in order to call a parameterized
     * constructor.
     *
     * @param preReaders
     *         the readers to execute before calling the constructor
     * @param constructor
     *         the constructor to use to create new instances
     */
    public RootReader(List<StateReader> preReaders, ObjectCreator<? extends T> constructor) {
        this.preReaders = preReaders;
        this.constructor = constructor;
        this.sectionReaders = new ArrayList<>();
    }

    @Override
    @Nullable
    public T read(@NotNull Context context) throws InputParsingException {
        for (StateReader preReader : preReaders) {
            preReader.read(context);
        }
        T obj = constructor.create(context);
        for (SectionReader<? super T> sectionReader : sectionReaders) {
            sectionReader.readAndSet(context, obj);
        }
        return obj;
    }

    /**
     * Creates a {@link ReaderBuilder} that will read as many tokens as necessary and store them in the given variables.
     * One can then create a {@link RootReader} by calling {@link ReaderBuilder#of(ObjectCreator)} or one of its
     * overloads, with a parameterized constructor using these variables.
     *
     * @param variableNames
     *         the names of the variables to read. The number of variables passed here determine the number of tokens
     *         consumed from the input.
     *
     * @return a new {@link ReaderBuilder} initialized with a {@link VariableReader} for the given variables.
     */
    public static ReaderBuilder withVars(String... variableNames) {
        return new ReaderBuilder().add(new VariableReader(variableNames));
    }

    /**
     * Creates a new {@code RootReader} that creates objects using the given constructor.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param <T>
     *         the type of objects that the new {@code RootReader} should create
     *
     * @return a new {@code RootReader}
     */
    public static <T> RootReader<T> of(Supplier<? extends T> constructor) {
        return new RootReader<>(ctx -> constructor.get());
    }

    /**
     * Creates a new {@code RootReader} that creates objects using the given constructor. This reader reads an integer
     * from the input in order to call the given constructor.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param <T>
     *         the type of objects that the new {@code RootReader} should create
     *
     * @return a new {@code RootReader}
     */
    public static <T> RootReader<T> of(Function<Integer, ? extends T> constructor) {
        return new RootReader<>(ctx -> constructor.apply(ctx.readInt()));
    }

    /**
     * Creates a new {@code RootReader} that creates objects using the given constructor. This reader reads 2 integers
     * from the input in order to call the given constructor.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param <T>
     *         the type of objects that the new {@code RootReader} should create
     *
     * @return a new {@code RootReader}
     */
    public static <T> RootReader<T> of(BiFunction<Integer, Integer, ? extends T> constructor) {
        return new RootReader<>(ctx -> constructor.apply(ctx.readInt(), ctx.readInt()));
    }

    /**
     * Creates a new {@code RootReader} that creates objects using the given constructor. This reader reads 3 integers
     * from the input in order to call the given constructor.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param <T>
     *         the type of objects that the new {@code RootReader} should create
     *
     * @return a new {@code RootReader}
     */
    public static <T> RootReader<T> of(Int3Creator<? extends T> constructor) {
        return new RootReader<>(constructor);
    }

    /**
     * Creates a new {@code RootReader} that creates objects using the given constructor. This reader reads 4 integers
     * from the input in order to call the given constructor.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param <T>
     *         the type of objects that the new {@code RootReader} should create
     *
     * @return a new {@code RootReader}
     */
    public static <T> RootReader<T> of(Int4Creator<T> constructor) {
        return new RootReader<>(constructor);
    }

    /**
     * Creates a new {@code RootReader} that creates objects using the given constructor. This reader reads 5 integers
     * from the input in order to call the given constructor.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param <T>
     *         the type of objects that the new {@code RootReader} should create
     *
     * @return a new {@code RootReader}
     */
    public static <T> RootReader<T> of(Int5Creator<T> constructor) {
        return new RootReader<>(constructor);
    }

    /**
     * Creates a new {@code RootReader} that creates objects using the given constructor. This reader reads 6 integers
     * from the input in order to call the given constructor.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param <T>
     *         the type of objects that the new {@code RootReader} should create
     *
     * @return a new {@code RootReader}
     */
    public static <T> RootReader<T> of(Int6Creator<T> constructor) {
        return new RootReader<>(constructor);
    }

    /**
     * Creates a new {@code RootReader} that creates objects using the given constructor. This reader reads 7 integers
     * from the input in order to call the given constructor.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param <T>
     *         the type of objects that the new {@code RootReader} should create
     *
     * @return a new {@code RootReader}
     */
    public static <T> RootReader<T> of(Int7Creator<T> constructor) {
        return new RootReader<>(constructor);
    }

    /**
     * Creates a new {@code RootReader} that creates objects using the given constructor.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param <T>
     *         the type of objects that the new {@code RootReader} should create
     *
     * @return a new {@code RootReader}
     */
    public static <T> RootReader<T> of(ObjectCreator<T> constructor) {
        return new RootReader<>(constructor);
    }

    /**
     * Tells this reader to map each element of the next line to a field of the created object or to a context variable,
     * or both.
     * <p>
     * The field/variable names are given as strings that can each be one of: <ul> <li>a field name (e.g.
     * "myField1")</li> <li>a '@' symbol followed by a variable name (e.g. "@N", "@myVar", "@123"...)</li> <li>both a
     * field name and a variable name separated by a '@' (e.g. "nItems@N", "size@nbOfSatellites"...)</li> </ul>
     * <p>
     * Note that "" describe neither a field nor a variable, and thus the corresponding entry in the line will be
     * ignored during parsing. Null descriptions and descriptions ending in '@' are forbidden.
     *
     * @param fieldAndVarNames
     *         an array of field/variable names, as described above
     *
     * @return this {@code RootReader}, for a convenient configuration syntax
     */
    public RootReader<T> fieldsAndVarsLine(String... fieldAndVarNames) {
        return section(new FieldsAndVarsLineReader<>(fieldAndVarNames));
    }

    /**
     * Reads one token and sets the given field on the object created by this reader.
     *
     * @param fieldName
     *         the name of the field to set
     *
     * @return this {@code RootReader}, for a convenient configuration syntax
     */
    public RootReader<T> field(String fieldName) {
        return section(new FieldAndVarReader<>(fieldName, null));
    }

    /**
     * Reads one token and sets the given context variable with the value.
     *
     * @param variableName
     *         the name of the variable to set.
     *
     * @return this {@code RootReader}, for a convenient configuration syntax
     */
    public RootReader<T> var(String variableName) {
        return section(new FieldAndVarReader<>(null, variableName));
    }

    /**
     * Reads one token and sets both the given field and the given context variable with the value.
     *
     * @param fieldName
     *         the name of the field to set
     * @param variableName
     *         the name of the variable to set
     *
     * @return this {@code RootReader}, for a convenient configuration syntax
     */
    public RootReader<T> fieldAndVar(String fieldName, String variableName) {
        return section(new FieldAndVarReader<>(fieldName, variableName));
    }

    /**
     * Consumes one token of the input without setting anything.
     *
     * @return this {@code RootReader}, for a convenient configuration syntax
     */
    public RootReader<T> skip() {
        return section((ctx, parent) -> ctx.readString());
    }

    /**
     * Tells this reader to create an array of ints from the next line, and set it on the object being created using the
     * provided setter.
     *
     * @param setter
     *         the setter to call on the object being created, with the created array
     *
     * @return this {@code RootReader}, for a convenient configuration syntax
     */
    public RootReader<T> intArrayLine(BiConsumer<? super T, int[]> setter) {
        return section(SectionReader.of(setter, LineReader.ints()));
    }

    /**
     * Tells this reader to create an array of longs from the next line, and set it on the object being created using
     * the provided setter.
     *
     * @param setter
     *         the setter to call on the object being created, with the created array
     *
     * @return this {@code RootReader}, for a convenient configuration syntax
     */
    public RootReader<T> longArrayLine(BiConsumer<? super T, long[]> setter) {
        return section(SectionReader.of(setter, LineReader.longs()));
    }

    /**
     * Tells this reader to create an array of doubles from the next line, and set it on the object being created using
     * the provided setter.
     *
     * @param setter
     *         the setter to call on the object being created, with the created array
     *
     * @return this {@code RootReader}, for a convenient configuration syntax
     */
    public RootReader<T> doubleArrayLine(BiConsumer<? super T, double[]> setter) {
        return section(SectionReader.of(setter, LineReader.doubles()));
    }

    /**
     * Tells this reader to create an array of strings from the next line, and set it on the object being created using
     * the provided setter.
     *
     * @param setter
     *         the setter to call on the object being created, with the created array
     *
     * @return this {@code RootReader}, for a convenient configuration syntax
     */
    public RootReader<T> stringArrayLine(BiConsumer<? super T, String[]> setter) {
        return section(SectionReader.of(setter, LineReader.strings()));
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
     * @return this {@code RootReader}, for a convenient configuration syntax
     */
    public <E> RootReader<T> arrayLine(BiConsumer<? super T, ? super E[]> setter, IntFunction<E[]> arrayCreator,
            Function<? super String, ? extends E> itemConverter) {
        return section(SectionReader.of(setter, LineReader.array(arrayCreator, itemConverter)));
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
     * @return this {@code RootReader}, for a convenient configuration syntax
     */
    public <E> RootReader<T> listLine(BiConsumer<? super T, ? super List<E>> setter,
            Function<String, ? extends E> itemConverter) {
        LineReader<List<E>> lineReader = LineReader.list(itemConverter);
        return section(SectionReader.of(setter, lineReader));
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
     * @return this {@code RootReader}, for a convenient configuration syntax
     */
    public <E> RootReader<T> array(BiConsumer<? super T, ? super E[]> setter, IntFunction<E[]> arrayCreator,
            String sizeVariable, ChildReader<? extends E, ? super T> itemReader) {
        return section(SectionReader.array(setter, arrayCreator, sizeGetter(sizeVariable), itemReader));
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
     * @return this {@code RootReader}, for a convenient configuration syntax
     */
    public <E> RootReader<T> array(BiConsumer<? super T, ? super E[]> setter, IntFunction<E[]> arrayCreator,
            Function<? super T, Integer> getSize, ChildReader<? extends E, ? super T> itemReader) {
        return section(SectionReader.array(setter, arrayCreator, sizeGetter(getSize), itemReader));
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
     * @return this {@code RootReader}, for a convenient configuration syntax
     */
    public <E> RootReader<T> array(BiConsumer<? super T, ? super E[]> setter, IntFunction<E[]> arrayCreator,
            BiFunction<? super T, Context, Integer> getSize, ChildReader<? extends E, ? super T> itemReader) {
        return section(SectionReader.array(setter, arrayCreator, getSize, itemReader));
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
     * @return this {@code RootReader}, for a convenient configuration syntax
     */
    public <E> RootReader<T> list(BiConsumer<? super T, ? super List<E>> setter, String sizeVariable,
            ChildReader<? extends E, ? super T> itemReader) {
        return section(SectionReader.list(setter, sizeGetter(sizeVariable), itemReader));
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
     * @return this {@code RootReader}, for a convenient configuration syntax
     */
    public <E> RootReader<T> list(BiConsumer<? super T, ? super List<E>> setter, Function<? super T, Integer> getSize,
            ChildReader<? extends E, ? super T> itemReader) {
        return section(SectionReader.list(setter, sizeGetter(getSize), itemReader));
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
     * @return this {@code RootReader}, for a convenient configuration syntax
     */
    public <E> RootReader<T> list(BiConsumer<? super T, ? super List<E>> setter,
            BiFunction<? super T, Context, Integer> getSize, ChildReader<? extends E, ? super T> itemReader) {
        return section(SectionReader.list(setter, getSize, itemReader));
    }

    /**
     * Tells this reader to create an object from the next few lines using the given reader, and set it on the object
     * being created using the given setter. The childReader will read as much input as necessary.
     *
     * @param setter
     *         the setter to call on the parent object being created, with the created child object
     * @param childReader
     *         a reader used to read the child object
     * @param <C>
     *         the type of object to create
     *
     * @return this {@code RootReader}, for a convenient configuration syntax
     */
    public <C> RootReader<T> objectSection(BiConsumer<? super T, ? super C> setter,
            ChildReader<? extends C, ? super T> childReader) {
        return section(new ObjectSectionReader<>(childReader, setter));
    }

    /**
     * Tells this reader to read the next few lines with the provided {@link SectionReader}. The given {@link
     * SectionReader} will read as much input as necessary.
     * <p>
     * The order matters, because each child section reader will be called in the order of insertion.
     * <p>
     * This is the most generic configuration method of {@code RootReader}, and most of the time you should use
     * the other configuration methods instead.
     *
     * @param sectionReader
     *         the section reader to use to read a part of the created object
     *
     * @return this {@code RootReader}, for a convenient configuration syntax
     */
    public RootReader<T> section(SectionReader<? super T> sectionReader) {
        sectionReaders.add(sectionReader);
        return this;
    }

    private static <P> BiFunction<P, Context, Integer> sizeGetter(String sizeVariable) {
        return (p, c) -> c.getVariableAsInt(sizeVariable);
    }

    private static <P> BiFunction<P, Context, Integer> sizeGetter(Function<? super P, Integer> getSizeFromParent) {
        return (p, c) -> getSizeFromParent.apply(p);
    }
}
