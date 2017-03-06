package org.hildan.hashcode.utils.parser.readers.line;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.hildan.hashcode.utils.parser.Context;
import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.config.Config;
import org.hildan.hashcode.utils.parser.conversion.StringConversionException;
import org.hildan.hashcode.utils.parser.conversion.StringConverter;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link SingleLineSectionReader} that reads the values as fields of the parent object, and/or context variables.
 *
 * @param <P>
 *         the type of parent that this {@code FieldsAndVarsLineReader} can update
 */
public class FieldsAndVarsLineReader<P> extends SingleLineSectionReader<P> {

    private final String[] fieldAndVarNames;

    private final String[] fields;

    private final String[] variables;

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
     * Note that "" and "@" describe neither a field nor a variable, and thus the corresponding entry in the line will
     * be ignored during parsing. A null description is forbidden.
     *
     * @param fieldAndVarNames
     *         an array of field/variable names, as described above
     */
    public FieldsAndVarsLineReader(String... fieldAndVarNames) {
        this.fieldAndVarNames = fieldAndVarNames;
        this.fields = new String[fieldAndVarNames.length];
        this.variables = new String[fieldAndVarNames.length];

        for (int i = 0; i < fieldAndVarNames.length; i++) {
            String name = fieldAndVarNames[i];
            String[] fieldAndVar = splitFieldAndVar(name);
            fields[i] = fieldAndVar[0];
            variables[i] = fieldAndVar[1];
        }
    }

    @NotNull
    private static String[] splitFieldAndVar(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null field/variable descriptions are not allowed");
        }
        if (!name.contains("@")) {
            return new String[]{name, ""};
        }
        return name.split("@", 2);
    }

    @Override
    public void setValues(P objectToFill, String[] values, Context context, Config config) {
        if (values.length != fieldAndVarNames.length) {
            throw new InputParsingException(
                    "The number of values doesn't match the expected fields: " + Arrays.toString(fieldAndVarNames));
        }
        for (int i = 0; i < fieldAndVarNames.length; i++) {
            if (!variables[i].isEmpty()) {
                context.setVariable(variables[i], values[i]);
            }
            if (!fields[i].isEmpty()) {
                setField(objectToFill, fields[i], values[i]);
            }
        }
    }

    private static void setField(Object obj, String fieldName, String value) throws InputParsingException {
        Class<?> clazz = obj.getClass();
        try {
            Field field = clazz.getDeclaredField(fieldName);
            setField(obj, field, value);
        } catch (NoSuchFieldException e) {
            throw new InputParsingException("The provided field name was not found in class " + clazz.getSimpleName(),
                    e);
        }
    }

    private static void setField(Object obj, Field field, String value) throws InputParsingException {
        try {
            field.setAccessible(true);
            field.set(obj, StringConverter.convert(field.getType(), value));
        } catch (StringConversionException e) {
            throw new InputParsingException(
                    "Type mismatch, cannot assign value '" + value + "' to field '" + field.getName() + "' of type "
                            + field.getType().getSimpleName());
        } catch (IllegalAccessException e) {
            throw new InputParsingException(
                    "Could not set field '" + field.getDeclaringClass().getSimpleName() + "." + field.getName()
                            + "' to value '" + value + "'", e);
        }
    }
}
