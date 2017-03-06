package org.hildan.hashcode.utils.parser.readers.line;

import java.util.Arrays;
import java.util.function.BiConsumer;

import org.hildan.hashcode.utils.parser.config.Config;
import org.hildan.hashcode.utils.parser.Context;

/**
 * A {@link SingleLineSectionReader} that reads the values as a single array of longs.
 *
 * @param <P>
 *         the type of parent that this {@code ArrayLineReader} can update
 */
public class LongArrayLineReader<P> extends SingleLineSectionReader<P> {

    private final BiConsumer<P, long[]> parentSetter;

    public LongArrayLineReader(BiConsumer<P, long[]> parentSetter) {
        this.parentSetter = parentSetter;
    }

    @Override
    protected void setValues(P objectToFill, String[] values, Context context, Config config) {
        long[] array = Arrays.stream(values).mapToLong(Long::parseLong).toArray();
        parentSetter.accept(objectToFill, array);
    }
}
