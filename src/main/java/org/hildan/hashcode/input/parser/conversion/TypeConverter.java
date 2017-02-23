package org.hildan.hashcode.input.parser.conversion;

import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

public class TypeConverter {

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

    @NotNull
    public static <T> T convertToPrimitiveWrapper(@NotNull Class<T> targetType, @NotNull String value) {
        if (!isWrapperType(targetType)) {
            throw new IllegalArgumentException(
                    "The provided type '" + targetType.getSimpleName() + "' is not a wrapper of primitive");
        }
        return targetType.cast(convert(targetType, value));
    }

    private static boolean isWrapperType(Class<?> clazz) {
        return WRAPPER_TYPES.contains(clazz);
    }

    @NotNull
    public static Object convert(@NotNull Class<?> targetType, @NotNull String value) throws TypeConversionException {
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
            } else if (targetType.equals(String.class)) {
                return value;
            }
        } catch (NumberFormatException e) {
            throw new TypeConversionException(targetType, value, e);
        }
        throw new TypeConversionException(targetType, value);
    }

    private static boolean convertToBoolean(@NotNull String value) {
        if (value.equalsIgnoreCase("false") || "0".equals(value)) {
            return false;
        }
        if (value.equalsIgnoreCase("true") || "1".equals(value)) {
            return true;
        }
        throw new TypeConversionException("Cannot convert value '" + value + "' to boolean");
    }
}
