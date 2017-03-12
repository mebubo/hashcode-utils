package org.hildan.hashcode.utils.parser.readers.section;

import java.util.function.BiConsumer;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.config.Config;
import org.hildan.hashcode.utils.parser.context.Context;
import org.hildan.hashcode.utils.parser.readers.ObjectReader;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link SectionReader} that reads a child object and sets it on the parent object, consuming as much input as
 * necessary.
 *
 * @param <T>
 *         the type of object that this {@code ObjectSectionReader} creates
 * @param <P>
 *         the type of parent on which this {@code ObjectSectionReader} sets the created object
 */
public class ObjectSectionReader<T, P> implements SectionReader<P> {

    private final ObjectReader<T> childReader;

    private final BiConsumer<P, T> parentSetter;

    /**
     * Creates a new {@code ObjectSectionReader}.
     *
     * @param childReader
     *         the reader to use to create the child object
     * @param parentSetter
     *         the setter to use to set the created child object on the parent object
     */
    public ObjectSectionReader(ObjectReader<T> childReader, BiConsumer<P, T> parentSetter) {
        this.childReader = childReader;
        this.parentSetter = parentSetter;
    }

    @Override
    public void readSection(@NotNull P objectToFill, @NotNull Context context, @NotNull Config config) throws
            InputParsingException {
        T value = childReader.read(context, config);
        parentSetter.accept(objectToFill, value);
    }
}
