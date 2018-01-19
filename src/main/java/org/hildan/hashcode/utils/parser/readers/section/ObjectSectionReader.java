package org.hildan.hashcode.utils.parser.readers.section;

import java.util.function.BiConsumer;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.context.Context;
import org.hildan.hashcode.utils.parser.readers.ChildReader;
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

    private final ChildReader<? extends T, ? super P> childReader;

    private final BiConsumer<? super P, ? super T> parentSetter;

    /**
     * Creates a new {@code ObjectSectionReader}.
     *
     * @param childReader
     *         the reader to use to create the child object
     * @param parentSetter
     *         the setter to use to set the created child object on the parent object
     */
    public ObjectSectionReader(ChildReader<? extends T, ? super P> childReader,
                               BiConsumer<? super P, ? super T> parentSetter) {
        this.childReader = childReader;
        this.parentSetter = parentSetter;
    }

    @Override
    public void readAndSet(@NotNull Context context, @NotNull P objectToFill) throws InputParsingException {
        T value = childReader.read(context, objectToFill);
        parentSetter.accept(objectToFill, value);
    }
}
