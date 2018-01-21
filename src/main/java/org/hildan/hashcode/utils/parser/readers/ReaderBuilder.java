package org.hildan.hashcode.utils.parser.readers;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.hildan.hashcode.utils.parser.context.Context;
import org.hildan.hashcode.utils.parser.readers.constructors.Int3Constructor;
import org.hildan.hashcode.utils.parser.readers.constructors.Int4Constructor;
import org.hildan.hashcode.utils.parser.readers.constructors.Int5Constructor;
import org.hildan.hashcode.utils.parser.readers.constructors.Int6Constructor;
import org.hildan.hashcode.utils.parser.readers.constructors.Int7Constructor;
import org.hildan.hashcode.utils.parser.readers.constructors.IntArrayConstructor;

/**
 * A builder that allows to create a {@link ObjectReader} with pre-readers that initialize some state (such as context
 * variables) before calling the constructor. This is useful when using parameterized constructors.
 */
public class ReaderBuilder {

    private final Consumer<Context> preReader;

    public ReaderBuilder(Consumer<Context> preReader) {
        this.preReader = preReader;
    }

    /**
     * Creates a new {@code ObjectReader} that creates objects with the given constructor. The argument passed to the
     * constructor is taken from the given context variable, that must be set up front via {@link
     * HCReader#withVars(String...)}.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param varName
     *         the variable to use as parameter to the given constructor
     * @param <T>
     *         the type of objects that the new {@code ObjectReader} should create
     *
     * @return a new {@code ObjectReader}
     */
    public <T> ObjectReader<T> of(Function<Integer, ? extends T> constructor, String varName) {
        return of(ctx -> constructor.apply(ctx.getVariableAsInt(varName)));
    }

    /**
     * Creates a new {@code ObjectReader} that creates objects with the given constructor. The arguments passed to the
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
     *         the type of objects that the new {@code ObjectReader} should create
     *
     * @return a new {@code ObjectReader}
     */
    public <T> ObjectReader<T> of(BiFunction<Integer, Integer, ? extends T> constructor, String var1, String var2) {
        return of(ctx -> constructor.apply(ctx.getVariableAsInt(var1), ctx.getVariableAsInt(var2)));
    }

    /**
     * Creates a new {@code ObjectReader} that creates objects with the given constructor. The arguments passed to the
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
     *         the type of objects that the new {@code ObjectReader} should create
     *
     * @return a new {@code ObjectReader}
     */
    public <T> ObjectReader<T> of(Int3Constructor<? extends T> constructor, String var1, String var2, String var3) {
        return of(ctx -> constructor.create(ctx.getVariableAsInt(var1), ctx.getVariableAsInt(var2),
                ctx.getVariableAsInt(var3)));
    }

    /**
     * Creates a new {@code ObjectReader} that creates objects with the given constructor. The arguments passed to the
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
     *         the type of objects that the new {@code ObjectReader} should create
     *
     * @return a new {@code ObjectReader}
     */
    public <T> ObjectReader<T> of(Int4Constructor<T> constructor, String var1, String var2, String var3, String var4) {
        return of(ctx -> constructor.create(ctx.getVariableAsInt(var1), ctx.getVariableAsInt(var2),
                ctx.getVariableAsInt(var3), ctx.getVariableAsInt(var4)));
    }

    /**
     * Creates a new {@code ObjectReader} that creates objects with the given constructor. The arguments passed to the
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
     *         the type of objects that the new {@code ObjectReader} should create
     *
     * @return a new {@code ObjectReader}
     */
    public <T> ObjectReader<T> of(Int5Constructor<T> constructor, String var1, String var2, String var3, String var4,
            String var5) {
        return of(ctx -> constructor.create(ctx.getVariableAsInt(var1), ctx.getVariableAsInt(var2),
                ctx.getVariableAsInt(var3), ctx.getVariableAsInt(var4), ctx.getVariableAsInt(var5)));
    }

    /**
     * Creates a new {@code ObjectReader} that creates objects with the given constructor. The arguments passed to the
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
     *         the type of objects that the new {@code ObjectReader} should create
     *
     * @return a new {@code ObjectReader}
     */
    public <T> ObjectReader<T> of(Int6Constructor<T> constructor, String var1, String var2, String var3, String var4,
            String var5, String var6) {
        return of(ctx -> constructor.create(ctx.getVariableAsInt(var1), ctx.getVariableAsInt(var2),
                ctx.getVariableAsInt(var3), ctx.getVariableAsInt(var4), ctx.getVariableAsInt(var5),
                ctx.getVariableAsInt(var6)));
    }

    /**
     * Creates a new {@code ObjectReader} that creates objects with the given constructor. The arguments passed to the
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
     *         the type of objects that the new {@code ObjectReader} should create
     *
     * @return a new {@code ObjectReader}
     */
    public <T> ObjectReader<T> of(Int7Constructor<T> constructor, String var1, String var2, String var3, String var4,
            String var5, String var6, String var7) {
        return of(ctx -> constructor.create(ctx.getVariableAsInt(var1), ctx.getVariableAsInt(var2),
                ctx.getVariableAsInt(var3), ctx.getVariableAsInt(var4), ctx.getVariableAsInt(var5),
                ctx.getVariableAsInt(var6), ctx.getVariableAsInt(var7)));
    }

    /**
     * Creates a new {@code ObjectReader} that creates objects with the given constructor. The arguments passed to the
     * constructor are taken from the given context variables, that must be set up front via {@link
     * HCReader#withVars(String...)}.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param vars
     *         the variables to use as parameters to the given constructor
     * @param <T>
     *         the type of objects that the new {@code ObjectReader} should create
     *
     * @return a new {@code ObjectReader}
     */
    public <T> ObjectReader<T> of(IntArrayConstructor<T> constructor, String... vars) {
        return of(ctx -> {
            int[] params = Arrays.stream(vars).mapToInt(ctx::getVariableAsInt).toArray();
            return constructor.create(params);
        });
    }

    /**
     * Creates a new {@code ObjectReader} that executes this variable's pre-readers before calling the given
     * constructor to create the object.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param <T>
     *         the type of objects that the new {@code ObjectReader} should create
     *
     * @return a new {@code ObjectReader}
     */
    public <T> ObjectReader<T> of(Function<Context, T> constructor) {
        return ctx -> {
            preReader.accept(ctx);
            return constructor.apply(ctx);
        };
    }
}
