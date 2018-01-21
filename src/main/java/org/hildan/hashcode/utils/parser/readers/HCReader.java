package org.hildan.hashcode.utils.parser.readers;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.hildan.hashcode.utils.parser.readers.variable.VariableReader;
import org.hildan.hashcode.utils.parser.readers.constructors.Int3Constructor;
import org.hildan.hashcode.utils.parser.readers.constructors.Int4Constructor;
import org.hildan.hashcode.utils.parser.readers.constructors.Int5Constructor;
import org.hildan.hashcode.utils.parser.readers.constructors.Int6Constructor;
import org.hildan.hashcode.utils.parser.readers.constructors.Int7Constructor;

public class HCReader {

    /**
     * Creates a {@link ReaderBuilder} that will read as many tokens as necessary to store them in the given variables.
     * One can then create an actual reader using the many methods of {@link ReaderBuilder}.
     *
     * @param variableNames
     *         the names of the variables to read. The number of variables passed here determines the number of tokens
     *         consumed from the input.
     *
     * @return a new {@link ReaderBuilder} initialized with a {@link VariableReader} for the given variables.
     */
    public static ReaderBuilder withVars(String... variableNames) {
        return new ReaderBuilder(new VariableReader(variableNames));
    }

    /**
     * Creates a new {@link ObjectReader} that creates objects using the given constructor.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param <T>
     *         the type of objects that the new {@link ObjectReader} should create
     *
     * @return a new {@link ObjectReader}
     */
    public static <T> ObjectReader<T> of(Supplier<? extends T> constructor) {
        return ctx -> constructor.get();
    }

    /**
     * Creates a new {@link ObjectReader} that creates objects using the given constructor. This reader reads an integer
     * from the input in order to call the given constructor.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param <T>
     *         the type of objects that the new {@link ObjectReader} should create
     *
     * @return a new {@link ObjectReader}
     */
    public static <T> ObjectReader<T> of(Function<Integer, ? extends T> constructor) {
        return ctx -> constructor.apply(ctx.readInt());
    }

    /**
     * Creates a new {@link ObjectReader} that creates objects using the given constructor. This reader reads 2 integers
     * from the input in order to call the given constructor.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param <T>
     *         the type of objects that the new {@link ObjectReader} should create
     *
     * @return a new {@link ObjectReader}
     */
    public static <T> ObjectReader<T> of(BiFunction<Integer, Integer, ? extends T> constructor) {
        return ctx -> constructor.apply(ctx.readInt(), ctx.readInt());
    }

    /**
     * Creates a new {@link ObjectReader} that creates objects using the given constructor. This reader reads 3 integers
     * from the input in order to call the given constructor.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param <T>
     *         the type of objects that the new {@link ObjectReader} should create
     *
     * @return a new {@link ObjectReader}
     */
    public static <T> ObjectReader<T> of(Int3Constructor<T> constructor) {
        return constructor;
    }

    /**
     * Creates a new {@link ObjectReader} that creates objects using the given constructor. This reader reads 4 integers
     * from the input in order to call the given constructor.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param <T>
     *         the type of objects that the new {@link ObjectReader} should create
     *
     * @return a new {@link ObjectReader}
     */
    public static <T> ObjectReader<T> of(Int4Constructor<T> constructor) {
        return constructor;
    }

    /**
     * Creates a new {@link ObjectReader} that creates objects using the given constructor. This reader reads 5 integers
     * from the input in order to call the given constructor.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param <T>
     *         the type of objects that the new {@link ObjectReader} should create
     *
     * @return a new {@link ObjectReader}
     */
    public static <T> ObjectReader<T> of(Int5Constructor<T> constructor) {
        return constructor;
    }

    /**
     * Creates a new {@link ObjectReader} that creates objects using the given constructor. This reader reads 6 integers
     * from the input in order to call the given constructor.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param <T>
     *         the type of objects that the new {@link ObjectReader} should create
     *
     * @return a new {@link ObjectReader}
     */
    public static <T> ObjectReader<T> of(Int6Constructor<T> constructor) {
        return constructor;
    }

    /**
     * Creates a new {@link ObjectReader} that creates objects using the given constructor. This reader reads 7 integers
     * from the input in order to call the given constructor.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param <T>
     *         the type of objects that the new {@link ObjectReader} should create
     *
     * @return a new {@link ObjectReader}
     */
    public static <T> ObjectReader<T> of(Int7Constructor<T> constructor) {
        return constructor;
    }
}
