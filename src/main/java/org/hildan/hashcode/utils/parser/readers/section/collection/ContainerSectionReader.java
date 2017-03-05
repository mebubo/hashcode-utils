package org.hildan.hashcode.utils.parser.readers.section.collection;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.config.Config;
import org.hildan.hashcode.utils.parser.Context;
import org.hildan.hashcode.utils.parser.readers.TreeObjectReader;
import org.hildan.hashcode.utils.parser.readers.section.BaseSectionReader;

/**
 * @param <E>
 *         element type
 * @param <C>
 *         collection type
 * @param <P>
 *         parent type
 */
public abstract class ContainerSectionReader<E, C, P> extends BaseSectionReader<C, P> {

    private final IntFunction<C> constructor;

    private final TreeObjectReader<E> itemReader;

    private final Function<P, Integer> getSizeFromParent;

    private final String sizeVariable;

    public ContainerSectionReader(IntFunction<C> constructor, TreeObjectReader<E> itemReader,
            Function<P, Integer> getSizeFromParent, BiConsumer<P, C> parentSetter) {
        super(parentSetter);
        this.constructor = constructor;
        this.itemReader = itemReader;
        this.getSizeFromParent = getSizeFromParent;
        this.sizeVariable = null;
    }

    public ContainerSectionReader(IntFunction<C> constructor, TreeObjectReader<E> itemReader, String sizeVariable,
            BiConsumer<P, C> parentSetter) {
        super(parentSetter);
        this.constructor = constructor;
        this.itemReader = itemReader;
        this.getSizeFromParent = null;
        this.sizeVariable = sizeVariable;
    }

    @Override
    public C readSectionValue(P objectToFill, Context context, Config config) throws InputParsingException {
        int size = getSize(objectToFill, context);
        C collection = constructor.apply(size);
        for (int i = 0; i < size; i++) {
            add(collection, i, itemReader.read(context, config));
        }
        return collection;
    }

    protected abstract void add(C collection, int index, E element);

    private int getSize(P objectToFill, Context context) {
        if (getSizeFromParent != null) {
            return getSizeFromParent.apply(objectToFill);
        } else {
            return getSizeFromContext(context);
        }
    }

    private int getSizeFromContext(Context context) {
        String size = context.getVariable(sizeVariable);
        try {
            return Integer.parseInt(size);
        } catch (NumberFormatException e) {
            throw new InputParsingException(
                    "Cannot get the size of the list, value '" + size + "' of variable '" + sizeVariable
                            + "' is not a number");
        }
    }
}
