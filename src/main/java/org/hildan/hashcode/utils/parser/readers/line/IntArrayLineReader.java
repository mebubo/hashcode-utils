package org.hildan.hashcode.utils.parser.readers.line;

import java.util.Arrays;
import java.util.function.BiConsumer;

import org.hildan.hashcode.utils.parser.config.Config;
import org.hildan.hashcode.utils.parser.context.Context;

/**
 * A {@link SingleLineSectionReader} that reads the values as a single array of ints.
 *
 * @param <P>
 *         the type of parent that this {@code ArrayLineReader} can update
 */
public class IntArrayLineReader<P> extends SingleLineSectionReader<P> {

    private final BiConsumer<? super P, int[]> parentSetter;

    public IntArrayLineReader(BiConsumer<? super P, int[]> parentSetter) {
        this.parentSetter = parentSetter;
    }

    @Override
    protected void setValues(P objectToFill, String[] values, Context context, Config config) {
        int[] array = Arrays.stream(values).mapToInt(Integer::parseInt).toArray();
        parentSetter.accept(objectToFill, array);
    }
}
