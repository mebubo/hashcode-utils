package org.hildan.hashcode.utils.parser.conversion;

import org.hildan.hashcode.utils.parser.InputParsingException;

/**
 * An exception thrown when the {@link StringConverter} cannot convert the given value to the desired type.
 */
public class StringConversionException extends InputParsingException {

    private final Class<?> targetType;

    private final String value;

    StringConversionException(Class<?> targetType, String value, String message) {
        super(message);
        this.targetType = targetType;
        this.value = value;
    }

    StringConversionException(Class<?> targetType, String value) {
        this(targetType, value, defaultMessage(targetType, value));
    }

    StringConversionException(Class<?> targetType, String value, Throwable e) {
        super(defaultMessage(targetType, value), e);
        this.targetType = targetType;
        this.value = value;
    }

    private static String defaultMessage(Class<?> targetType, String value) {
        return String.format("Cannot convert value '%s' to type '%s'", value, targetType.getSimpleName());
    }

    /**
     * Gets the attempted target type for the conversion.
     *
     * @return the type to which the value could not be converted
     */
    public Class<?> getTargetType() {
        return targetType;
    }

    /**
     * Gets the value that failed to be converted.
     *
     * @return the value that could not be converted
     */
    public String getValue() {
        return value;
    }
}
