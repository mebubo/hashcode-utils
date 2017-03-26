package org.hildan.hashcode.utils.parser.reflect;

import java.lang.reflect.Field;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReflectUtils {

    public static void setField(@NotNull Object obj, @NotNull String fieldName, @Nullable Object value) throws
            InputParsingException {
        Class<?> clazz = obj.getClass();
        try {
            Field field = clazz.getDeclaredField(fieldName);
            setField(obj, field, value);
        } catch (NoSuchFieldException e) {
            throw new InputParsingException(
                    String.format("The provided field name '%s' was not found in class '%s'", fieldName,
                            clazz.getSimpleName()), e);
        }
    }

    private static void setField(@NotNull Object obj, @NotNull Field field, @Nullable Object value) throws
            InputParsingException {
        try {
            field.setAccessible(true);
            field.set(obj, convertIfNecessary(field, value));
        } catch (IllegalAccessException e) {
            throw new InputParsingException(
                    "Could not set field '" + field.getDeclaringClass().getSimpleName() + "." + field.getName()
                            + "' to value '" + value + "'", e);
        }
    }

    @Contract("_, null -> null; _, !null -> !null")
    private static Object convertIfNecessary(@NotNull Field field, @Nullable Object value) throws
            InputParsingException {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return convertString(field, (String) value);
        }
        if (!field.getType().isInstance(value)) {
            throw new InputParsingException(
                    String.format("Type mismatch, cannot assign value '%s' of type %s to field '%s' of type %s", value,
                            value.getClass().getSimpleName(), field.getName(), field.getType().getSimpleName()));
        }
        return value;
    }

    @NotNull
    private static Object convertString(@NotNull Field field, @NotNull String value) throws InputParsingException {
        try {
            return StringConverter.convert(field.getType(), value);
        } catch (StringConversionException e) {
            throw new InputParsingException(
                    "Type mismatch, cannot assign value '" + value + "' to field '" + field.getName() + "' of type "
                            + field.getType().getSimpleName(), e);
        }
    }
}
