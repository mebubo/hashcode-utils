package org.hildan.hashcode.input.parser.readers.line;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.hildan.hashcode.input.InputParsingException;
import org.hildan.hashcode.input.config.Config;
import org.hildan.hashcode.input.parser.Context;
import org.hildan.hashcode.input.parser.conversion.TypeConversionException;
import org.hildan.hashcode.input.parser.conversion.TypeConverter;

public class FieldsLineReader<P> extends SingleLineSectionReader<P> {

    private final String[] fieldNames;

    public FieldsLineReader(String... fieldNames) {
        this.fieldNames = fieldNames;
    }

    @Override
    public void setValues(P objectToFill, String[] values, Context context, Config config) {
        if (values.length != fieldNames.length) {
            throw new InputParsingException(
                    "The number of values doesn't match the expected fields: " + Arrays.toString(fieldNames));
        }
        for (int f = 0; f < fieldNames.length; f++) {
            String name = fieldNames[f];
            String fieldName = extractFieldName(name);
            String variableName = extractVariableName(name);
            if (variableName != null) {
                context.setVariable(variableName, values[f]);
            }
            if (fieldName != null) {
                setField(objectToFill, fieldName, values[f]);
            }
        }
    }

    private static String extractFieldName(String name) {
        if (!name.contains("@")) {
            return name;
        }
        return name.split("@")[0];
    }

    private static String extractVariableName(String name) {
        if (!name.contains("@")) {
            return null;
        }
        return name.split("@")[1];
    }

    private static void setField(Object obj, String fieldName, String value) {
        Class<?> clazz = obj.getClass();
        try {
            Field field = clazz.getDeclaredField(fieldName);
            setField(obj, field, value);
        } catch (NoSuchFieldException e) {
            throw new InputParsingException(
                    "The provided field name was not found in class " + clazz.getSimpleName(), e);
        }
    }

    private static void setField(Object obj, Field field, String value) {
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
