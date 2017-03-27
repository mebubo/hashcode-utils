package org.hildan.hashcode.utils.parser.readers.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.hildan.hashcode.utils.parser.readers.RootReader;
import org.hildan.hashcode.utils.parser.readers.creators.Int3Creator;
import org.hildan.hashcode.utils.parser.readers.creators.Int4Creator;
import org.hildan.hashcode.utils.parser.readers.creators.Int5Creator;
import org.hildan.hashcode.utils.parser.readers.creators.Int6Creator;
import org.hildan.hashcode.utils.parser.readers.creators.Int7Creator;
import org.hildan.hashcode.utils.parser.readers.creators.IntArrayCreator;
import org.hildan.hashcode.utils.parser.readers.creators.ObjectCreator;

/**
 * A builder that allows to create a {@link RootReader} with pre-readers that initialize some state (such as context
 * variables) before calling the constructor. This is useful when using parameterized constructors.
 */
public class ReaderBuilder {

    private final List<StateReader> preReaders = new ArrayList<>();

    /**
     * Adds a pre-reader to this builder, to be be passed on to the {@link RootReader} when created.
     *
     * @param reader
     *         a reader to be executed by the {@link RootReader} before creating an object
     *
     * @return this builder, for convenient chaining
     */
    public ReaderBuilder add(StateReader reader) {
        preReaders.add(reader);
        return this;
    }

    /**
     * Creates a new {@code RootReader} that creates objects with the given constructor. The argument passed to the
     * constructor is taken from the given context variable, that must be set up front via {@link
     * RootReader#withVars(String...)}.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param varName
     *         the variable to use as parameter to the given constructor
     * @param <T>
     *         the type of objects that the new {@code RootReader} should create
     *
     * @return a new {@code RootReader}
     */
    public <T> RootReader<T> of(Function<Integer, ? extends T> constructor, String varName) {
        return of(ctx -> constructor.apply(ctx.getVariableAsInt(varName)));
    }

    /**
     * Creates a new {@code RootReader} that creates objects with the given constructor. The arguments passed to the
     * constructor are taken from the given context variables, that must be set up front via {@link
     * RootReader#withVars(String...)}.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param var1
     *         the variable to use as 1st parameter to the given constructor
     * @param var2
     *         the variable to use as 2nd parameter to the given constructor
     * @param <T>
     *         the type of objects that the new {@code RootReader} should create
     *
     * @return a new {@code RootReader}
     */
    public <T> RootReader<T> of(BiFunction<Integer, Integer, ? extends T> constructor, String var1, String var2) {
        return of(ctx -> constructor.apply(ctx.getVariableAsInt(var1), ctx.getVariableAsInt(var2)));
    }

    /**
     * Creates a new {@code RootReader} that creates objects with the given constructor. The arguments passed to the
     * constructor are taken from the given context variables, that must be set up front via {@link
     * RootReader#withVars(String...)}.
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
     *         the type of objects that the new {@code RootReader} should create
     *
     * @return a new {@code RootReader}
     */
    public <T> RootReader<T> of(Int3Creator<? extends T> constructor, String var1, String var2, String var3) {
        return of(ctx -> constructor.create(ctx.getVariableAsInt(var1), ctx.getVariableAsInt(var2),
                ctx.getVariableAsInt(var3)));
    }

    /**
     * Creates a new {@code RootReader} that creates objects with the given constructor. The arguments passed to the
     * constructor are taken from the given context variables, that must be set up front via {@link
     * RootReader#withVars(String...)}.
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
     *         the type of objects that the new {@code RootReader} should create
     *
     * @return a new {@code RootReader}
     */
    public <T> RootReader<T> of(Int4Creator<T> constructor, String var1, String var2, String var3, String var4) {
        return of(ctx -> constructor.create(ctx.getVariableAsInt(var1), ctx.getVariableAsInt(var2),
                ctx.getVariableAsInt(var3), ctx.getVariableAsInt(var4)));
    }

    /**
     * Creates a new {@code RootReader} that creates objects with the given constructor. The arguments passed to the
     * constructor are taken from the given context variables, that must be set up front via {@link
     * RootReader#withVars(String...)}.
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
     *         the type of objects that the new {@code RootReader} should create
     *
     * @return a new {@code RootReader}
     */
    public <T> RootReader<T> of(Int5Creator<T> constructor, String var1, String var2, String var3, String var4,
                                String var5) {
        return of(ctx -> constructor.create(ctx.getVariableAsInt(var1), ctx.getVariableAsInt(var2),
                ctx.getVariableAsInt(var3), ctx.getVariableAsInt(var4), ctx.getVariableAsInt(var5)));
    }

    /**
     * Creates a new {@code RootReader} that creates objects with the given constructor. The arguments passed to the
     * constructor are taken from the given context variables, that must be set up front via {@link
     * RootReader#withVars(String...)}.
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
     *         the type of objects that the new {@code RootReader} should create
     *
     * @return a new {@code RootReader}
     */
    public <T> RootReader<T> of(Int6Creator<T> constructor, String var1, String var2, String var3, String var4,
                                String var5, String var6) {
        return of(ctx -> constructor.create(ctx.getVariableAsInt(var1), ctx.getVariableAsInt(var2),
                ctx.getVariableAsInt(var3), ctx.getVariableAsInt(var4), ctx.getVariableAsInt(var5),
                ctx.getVariableAsInt(var6)));
    }

    /**
     * Creates a new {@code RootReader} that creates objects with the given constructor. The arguments passed to the
     * constructor are taken from the given context variables, that must be set up front via {@link
     * RootReader#withVars(String...)}.
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
     *         the type of objects that the new {@code RootReader} should create
     *
     * @return a new {@code RootReader}
     */
    public <T> RootReader<T> of(Int7Creator<T> constructor, String var1, String var2, String var3, String var4,
                                String var5, String var6, String var7) {
        return of(ctx -> constructor.create(ctx.getVariableAsInt(var1), ctx.getVariableAsInt(var2),
                ctx.getVariableAsInt(var3), ctx.getVariableAsInt(var4), ctx.getVariableAsInt(var5),
                ctx.getVariableAsInt(var6), ctx.getVariableAsInt(var7)));
    }

    /**
     * Creates a new {@code RootReader} that creates objects with the given constructor. The arguments passed to the
     * constructor are taken from the given context variables, that must be set up front via {@link
     * RootReader#withVars(String...)}.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param vars
     *         the variables to use as parameters to the given constructor
     * @param <T>
     *         the type of objects that the new {@code RootReader} should create
     *
     * @return a new {@code RootReader}
     */
    public <T> RootReader<T> of(IntArrayCreator<T> constructor, String... vars) {
        return of(ctx -> {
            int[] params = new int[vars.length];
            for (int i = 0; i < vars.length; i++) {
                params[i] = ctx.getVariableAsInt(vars[i]);
            }
            return constructor.create(params);
        });
    }

    /**
     * Creates a new {@code RootReader} that executes this builder's pre-readers before calling the given constructor to
     * create the object.
     *
     * @param constructor
     *         the constructor to use to create new instances
     * @param <T>
     *         the type of objects that the new {@code RootReader} should create
     *
     * @return a new {@code RootReader}
     */
    public <T> RootReader<T> of(ObjectCreator<T> constructor) {
        return new RootReader<>(preReaders, constructor);
    }
}
