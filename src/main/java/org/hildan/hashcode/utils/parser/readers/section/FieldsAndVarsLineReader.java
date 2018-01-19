package org.hildan.hashcode.utils.parser.readers.section;

import java.util.ArrayList;
import java.util.List;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.context.Context;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link SectionReader} that reads the values as fields of the parent object, and/or context variables.
 *
 * @param <P>
 *         the type of parent that this {@code FieldsAndVarsLineReader} can update
 */
public class FieldsAndVarsLineReader<P> implements SectionReader<P> {

    private final List<FieldAndVarReader<P>> readers;

    /**
     * Creates a new {@code FieldsAndVarsLineReader} with the given field/variable names.
     * <p>
     * The field/variable names are given as strings that can each be one of:
     * <ul>
     * <li>a field name (e.g. "myField1")</li>
     * <li>a '@' symbol followed by a variable name (e.g. "@N", "@myVar", "@123"...)</li>
     * <li>both a field name and a variable name separated by a '@' (e.g. "nItems@N", "size@nbOfSatellites"...)</li>
     * </ul>
     * <p>
     * Note that "" describe neither a field nor a variable, and thus the corresponding entry in the line will be
     * ignored during parsing. Null descriptions and descriptions ending in '@' are forbidden.
     *
     * @param fieldAndVarNames
     *         an array of field/variable names, as described above
     */
    public FieldsAndVarsLineReader(String... fieldAndVarNames) {
        this.readers = new ArrayList<>(fieldAndVarNames.length);

        for (String name : fieldAndVarNames) {
            String[] fieldAndVar = splitFieldAndVar(name);
            readers.add(new FieldAndVarReader<>(fieldAndVar[0], fieldAndVar[1]));
        }
    }

    @NotNull
    private static String[] splitFieldAndVar(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null field/variable descriptions are not allowed");
        }
        if (name.isEmpty()) {
            return new String[]{null, null};
        }
        if (!name.contains("@")) {
            return new String[]{name, null};
        }
        String[] fieldAndVar = name.split("@", 2);
        nullifyEmptyStrings(fieldAndVar);
        return fieldAndVar;
    }

    private static void nullifyEmptyStrings(String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[0] != null && array[0].isEmpty()) {
                array[0] = null;
            }
        }
    }

    @Override
    public void readAndSet(@NotNull Context context, @NotNull P parent) throws InputParsingException {
        for (FieldAndVarReader<P> reader : readers) {
            reader.readAndSet(context, parent);
        }
    }
}
