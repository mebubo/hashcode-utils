package org.hildan.hashcode.utils.parser.conversion;

import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

/**
 * A converter that turns strings into primitive types or primitive wrapper types. It also supports conversion to
 * String, which is a no-op.
 */
public class StringConverter {

    private static final Set<Class<?>> WRAPPER_TYPES;

    static {
        WRAPPER_TYPES = new HashSet<>();
        WRAPPER_TYPES.add(Boolean.class);
        WRAPPER_TYPES.add(Character.class);
        WRAPPER_TYPES.add(Byte.class);
        WRAPPER_TYPES.add(Short.class);
        WRAPPER_TYPES.add(Integer.class);
        WRAPPER_TYPES.add(Long.class);
        WRAPPER_TYPES.add(Float.class);
        WRAPPER_TYPES.add(Double.class);
    }

    /**
     * Converts the given string value into the given primitive wrapper type.
     *
     * @param targetType
     *         the primitive wrapper type to convert the value to
     * @param value
     *         the string value to convert
     * @param <T>
     *         the target type of the conversion
     *
     * @return the converted value
     * @throws IllegalArgumentException
     *         if the provided targetType is not a primitive wrapper type
     */
    @NotNull
    public static <T> T convertToPrimitiveWrapper(@NotNull Class<T> targetType, @NotNull String value)
            throws IllegalArgumentException {
        if (!isWrapperType(targetType)) {
            throw new IllegalArgumentException(
                    "The provided type '" + targetType.getSimpleName() + "' is not a wrapper of primitive");
        }
        return targetType.cast(convert(targetType, value));
    }

    private static boolean isWrapperType(@NotNull Class<?> clazz) {
        return WRAPPER_TYPES.contains(clazz);
    }

    /**
     * Converts the given string value into the primitive wrapper type corresponding to the given type. Since this
     * method is generic, it cannot convert to a primitive type, but if the given type is a primitive type, then the
     * corresponding wrapper type is returned. If the targetType is String, then this method just returns its input.
     *
     * @param targetType
     *         the target type of the conversion. Must be a primitive type, a primitive wrapper type, or String.
     * @param value
     *         the string value to convert
     *
     * @return the converted value as an object of the target type. This has to be returned as an Object due to the java
     * language limitation (we cannot return primitives in generics).
     * @throws StringConversionException
     *         if the string value cannot be converted to the given type, or if the target type is not supported
     */
    @NotNull
    public static Object convert(@NotNull Class<?> targetType, @NotNull String value) throws StringConversionException {
        try {
            if (targetType.equals(boolean.class) || targetType.equals(Boolean.class)) {
                return convertToBoolean(value);
            } else if (targetType.equals(long.class) || targetType.equals(Long.class)) {
                return Long.valueOf(value);
            } else if (targetType.equals(int.class) || targetType.equals(Integer.class)) {
                return Integer.valueOf(value);
            } else if (targetType.equals(double.class) || targetType.equals(Double.class)) {
                return Double.valueOf(value);
            } else if (targetType.equals(float.class) || targetType.equals(Float.class)) {
                return Float.valueOf(value);
            } else if (targetType.equals(char.class) || targetType.equals(Character.class)) {
                if (value.length() != 1) {
                    throw new StringConversionException(targetType, value);
                }
                return value.charAt(0);
            } else if (targetType.equals(String.class)) {
                return value;
            }
        } catch (NumberFormatException e) {
            throw new StringConversionException(targetType, value, e);
        }
        throw new StringConversionException(targetType, value);
    }

    private static boolean convertToBoolean(@NotNull String value) {
        if (value.equalsIgnoreCase("false") || "0".equals(value)) {
            return false;
        }
        if (value.equalsIgnoreCase("true") || "1".equals(value)) {
            return true;
        }
        throw new StringConversionException("Cannot convert value '" + value + "' to boolean");
    }
}
