package org.hildan.hashcode.input.parser.readers.line;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.hildan.hashcode.input.InputParsingException;
import org.hildan.hashcode.input.config.Config;
import org.hildan.hashcode.input.parser.conversion.TypeConversionException;
import org.hildan.hashcode.input.parser.conversion.TypeConverter;

public class FieldsLineReader<P> extends SingleLineSectionReader<P> {

    private final String[] fieldNames;

    public FieldsLineReader(String... fieldNames) {
        this.fieldNames = fieldNames;
    }

    @Override
    public void setValues(P objectToFill, String[] values, Config config) {
        setFields(objectToFill, values);
    }

    private void setFields(P obj, String[] values) {
        if (values.length != fieldNames.length) {
            throw new InputParsingException(
                    "The number of values doesn't match the expected fields: " + Arrays.toString(fieldNames));
        }
        Class<?> clazz = obj.getClass();
        for (int f = 0; f < fieldNames.length; f++) {
            try {
                Field field = clazz.getDeclaredField(fieldNames[f]);
                setField(obj, values[f], field);
            } catch (NoSuchFieldException e) {
                throw new InputParsingException(
                        "The provided field name was not found in class " + clazz.getSimpleName(), e);
            }
        }
    }

    private static <P> void setField(P obj, String value, Field field) {
        try {
            field.setAccessible(true);
            field.set(obj, TypeConverter.convert(field.getType(), value));
        } catch (TypeConversionException e) {
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
