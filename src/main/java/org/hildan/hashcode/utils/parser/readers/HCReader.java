package org.hildan.hashcode.utils.parser.readers;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.hildan.hashcode.utils.parser.context.Context;
import org.hildan.hashcode.utils.parser.readers.constructors.Int2Constructor;
import org.hildan.hashcode.utils.parser.readers.constructors.Int3Constructor;
import org.hildan.hashcode.utils.parser.readers.constructors.Int4Constructor;
import org.hildan.hashcode.utils.parser.readers.constructors.Int5Constructor;
import org.hildan.hashcode.utils.parser.readers.constructors.Int6Constructor;
import org.hildan.hashcode.utils.parser.readers.constructors.Int7Constructor;
import org.hildan.hashcode.utils.parser.readers.constructors.IntArrayConstructor;
import org.hildan.hashcode.utils.parser.readers.variable.VariableReader;

public class HCReader {

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
    public static <T> ObjectReader<T> create(Supplier<? extends T> constructor) {
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
    public static <T> ObjectReader<T> createFromInt(Function<Integer, ? extends T> constructor) {
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
    public static <T> ObjectReader<T> createFrom2Ints(Int2Constructor<T> constructor) {
        return constructor;
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
    public static <T> ObjectReader<T> createFrom3Ints(Int3Constructor<T> constructor) {
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
    public static <T> ObjectReader<T> createFrom4Ints(Int4Constructor<T> constructor) {
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
    public static <T> ObjectReader<T> createFrom5Ints(Int5Constructor<T> constructor) {
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
    public static <T> ObjectReader<T> createFrom6Ints(Int6Constructor<T> constructor) {
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
    public static <T> ObjectReader<T> createFrom7Ints(Int7Constructor<T> constructor) {
        return constructor;
    }

    /**
     * Creates a new {@link ObjectReader} that creates objects using the given constructor. This reader reads n integers
     * from the input and stores them in an array in order to call the given constructor.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param nbIntsToRead
     *         the number of integers to read from the input into the array passed to the constructor. Must be positive.
     * @param <T>
     *         the type of objects that the new {@link ObjectReader} should create
     *
     * @return a new {@link ObjectReader}
     */
    public static <T> ObjectReader<T> createFromInts(IntArrayConstructor<T> constructor, int nbIntsToRead) {
        if (nbIntsToRead <= 0) {
            throw new IllegalArgumentException("nbIntsToRead must be positive");
        }
        return ctx -> {
            int[] params = IntStream.range(0, nbIntsToRead).map(i -> ctx.readInt()).toArray();
            return constructor.create(params);
        };
    }

    /**
     * Creates an {@link HCReader.WithVars} initialized with the given variable names. From there, one can create a
     * reader constructing objects from some of these already stored variables instead of directly from the input.
     *
     * @param variableNames
     *         the names of the variables to read. The number of variables passed here determines the number of tokens
     *         consumed from the input.
     *
     * @return a new {@link HCReader.WithVars} initialized with a {@link VariableReader} for the given variables.
     */
    public static WithVars withVars(String... variableNames) {
        return new WithVars(variableNames);
    }

    /**
     * An {@link ObjectReader} factory that creates reader that start by reading variables. The created readers
     * usually create objects by passing some of these variables to their constructors.
     */
    public static class WithVars {

        private final Consumer<Context> variableReader;

        public WithVars(String... variableNames) {
            this.variableReader = new VariableReader(variableNames);
        }

        /**
         * Creates a new {@link ObjectReader} that first read some variables before calling the given function.
         *
         * @param constructor
         *         the constructor to use to create new instances. This function may access the variables that this
         *         factory provides
         * @param <T>
         *         the type of objects that the new {@link ObjectReader} should create
         *
         * @return a new {@link ObjectReader}
         */
        public <T> ObjectReader<T> of(Function<Context, T> constructor) {
            return ctx -> {
                variableReader.accept(ctx);
                return constructor.apply(ctx);
            };
        }

        /**
         * Creates a new {@link ObjectReader} that creates objects with the given constructor. The argument passed to
         * the
         * constructor is taken from the given context variable, that must be set up front via {@link
         * HCReader#withVars(String...)}.
         *
         * @param constructor
         *         the constructor to use to create new instances
         * @param varName
         *         the variable to use as parameter to the given constructor
         * @param <T>
         *         the type of objects that the new {@link ObjectReader} should create
         *
         * @return a new {@link ObjectReader}
         */
        public <T> ObjectReader<T> createFromVar(Function<Integer, T> constructor, String varName) {
            return of(ctx -> constructor.apply(ctx.getVariableAsInt(varName)));
        }

        /**
         * Creates a new {@link ObjectReader} that creates objects with the given constructor. The arguments passed
         * to the
         * constructor are taken from the given context variables, that must be set up front via {@link
         * HCReader#withVars(String...)}.
         *
         * @param constructor
         *         the constructor to use to create new instances
         * @param var1
         *         the variable to use as 1st parameter to the given constructor
         * @param var2
         *         the variable to use as 2nd parameter to the given constructor
         * @param <T>
         *         the type of objects that the new {@link ObjectReader} should create
         *
         * @return a new {@link ObjectReader}
         */
        public <T> ObjectReader<T> createFrom2Vars(Int2Constructor<T> constructor, String var1, String var2) {
            return of(ctx -> constructor.create(ctx.getVariableAsInt(var1), ctx.getVariableAsInt(var2)));
        }

        /**
         * Creates a new {@link ObjectReader} that creates objects with the given constructor. The arguments passed
         * to the
         * constructor are taken from the given context variables, that must be set up front via {@link
         * HCReader#withVars(String...)}.
         *
         * @param constructor
         *         the constructor to use to create new instances
         * @param var1
         *         the variable to use as 1st parameter to the given constructor
         * @param var2
         *         the variable to use as 2nd parameter to the given constructor
         * @param var3
         *         the variable to use as 3rd parameter to the given constructor
         * @param <T>
         *         the type of objects that the new {@link ObjectReader} should create
         *
         * @return a new {@link ObjectReader}
         */
        public <T> ObjectReader<T> createFrom3Vars(Int3Constructor<T> constructor, String var1, String var2,
                String var3) {
            return of(ctx -> constructor.create(ctx.getVariableAsInt(var1), ctx.getVariableAsInt(var2),
                    ctx.getVariableAsInt(var3)));
        }

        /**
         * Creates a new {@link ObjectReader} that creates objects with the given constructor. The arguments passed
         * to the
         * constructor are taken from the given context variables, that must be set up front via {@link
         * HCReader#withVars(String...)}.
         *
         * @param constructor
         *         the constructor to use to create new instances
         * @param var1
         *         the variable to use as 1st parameter to the given constructor
         * @param var2
         *         the variable to use as 2nd parameter to the given constructor
         * @param var3
         *         the variable to use as 3rd parameter to the given constructor
         * @param var4
         *         the variable to use as 4th parameter to the given constructor
         * @param <T>
         *         the type of objects that the new {@link ObjectReader} should create
         *
         * @return a new {@link ObjectReader}
         */
        public <T> ObjectReader<T> createFrom4Vars(Int4Constructor<T> constructor, String var1, String var2,
                String var3, String var4) {
            return of(ctx -> constructor.create(ctx.getVariableAsInt(var1), ctx.getVariableAsInt(var2),
                    ctx.getVariableAsInt(var3), ctx.getVariableAsInt(var4)));
        }

        /**
         * Creates a new {@link ObjectReader} that creates objects with the given constructor. The arguments passed
         * to the
         * constructor are taken from the given context variables, that must be set up front via {@link
         * HCReader#withVars(String...)}.
         *
         * @param constructor
         *         the constructor to use to create new instances
         * @param var1
         *         the variable to use as 1st parameter to the given constructor
         * @param var2
         *         the variable to use as 2nd parameter to the given constructor
         * @param var3
         *         the variable to use as 3rd parameter to the given constructor
         * @param var4
         *         the variable to use as 4th parameter to the given constructor
         * @param var5
         *         the variable to use as 5th parameter to the given constructor
         * @param <T>
         *         the type of objects that the new {@link ObjectReader} should create
         *
         * @return a new {@link ObjectReader}
         */
        public <T> ObjectReader<T> createFrom5Vars(Int5Constructor<T> constructor, String var1, String var2,
                String var3, String var4, String var5) {
            return of(ctx -> constructor.create(ctx.getVariableAsInt(var1), ctx.getVariableAsInt(var2),
                    ctx.getVariableAsInt(var3), ctx.getVariableAsInt(var4), ctx.getVariableAsInt(var5)));
        }

        /**
         * Creates a new {@link ObjectReader} that creates objects with the given constructor. The arguments passed
         * to the
         * constructor are taken from the given context variables, that must be set up front via {@link
         * HCReader#withVars(String...)}.
         *
         * @param constructor
         *         the constructor to use to create new instances
         * @param var1
         *         the variable to use as 1st parameter to the given constructor
         * @param var2
         *         the variable to use as 2nd parameter to the given constructor
         * @param var3
         *         the variable to use as 3rd parameter to the given constructor
         * @param var4
         *         the variable to use as 4th parameter to the given constructor
         * @param var5
         *         the variable to use as 5th parameter to the given constructor
         * @param var6
         *         the variable to use as 6th parameter to the given constructor
         * @param <T>
         *         the type of objects that the new {@link ObjectReader} should create
         *
         * @return a new {@link ObjectReader}
         */
        public <T> ObjectReader<T> createFrom6Vars(Int6Constructor<T> constructor, String var1, String var2,
                String var3, String var4, String var5, String var6) {
            return of(ctx -> constructor.create(ctx.getVariableAsInt(var1), ctx.getVariableAsInt(var2),
                    ctx.getVariableAsInt(var3), ctx.getVariableAsInt(var4), ctx.getVariableAsInt(var5),
                    ctx.getVariableAsInt(var6)));
        }

        /**
         * Creates a new {@link ObjectReader} that creates objects with the given constructor. The arguments passed
         * to the
         * constructor are taken from the given context variables, that must be set up front via {@link
         * HCReader#withVars(String...)}.
         *
         * @param constructor
         *         the constructor to use to create new instances
         * @param var1
         *         the variable to use as 1st parameter to the given constructor
         * @param var2
         *         the variable to use as 2nd parameter to the given constructor
         * @param var3
         *         the variable to use as 3rd parameter to the given constructor
         * @param var4
         *         the variable to use as 4th parameter to the given constructor
         * @param var5
         *         the variable to use as 5th parameter to the given constructor
         * @param var6
         *         the variable to use as 6th parameter to the given constructor
         * @param var7
         *         the variable to use as 7th parameter to the given constructor
         * @param <T>
         *         the type of objects that the new {@link ObjectReader} should create
         *
         * @return a new {@link ObjectReader}
         */
        public <T> ObjectReader<T> createFrom7Vars(Int7Constructor<T> constructor, String var1, String var2,
                String var3, String var4, String var5, String var6, String var7) {
            return of(ctx -> constructor.create(ctx.getVariableAsInt(var1), ctx.getVariableAsInt(var2),
                    ctx.getVariableAsInt(var3), ctx.getVariableAsInt(var4), ctx.getVariableAsInt(var5),
                    ctx.getVariableAsInt(var6), ctx.getVariableAsInt(var7)));
        }

        /**
         * Creates a new {@link ObjectReader} that creates objects with the given constructor. The arguments passed
         * to the
         * constructor are taken from the given context variables, that must be set up front via {@link
         * HCReader#withVars(String...)}.
         *
         * @param constructor
         *         the constructor to use to create new instances
         * @param vars
         *         the variables to use as parameters to the given constructor
         * @param <T>
         *         the type of objects that the new {@link ObjectReader} should create
         *
         * @return a new {@link ObjectReader}
         */
        public <T> ObjectReader<T> createFromVars(IntArrayConstructor<T> constructor, String... vars) {
            return of(ctx -> {
                int[] params = Arrays.stream(vars).mapToInt(ctx::getVariableAsInt).toArray();
                return constructor.create(params);
            });
        }
    }
}
