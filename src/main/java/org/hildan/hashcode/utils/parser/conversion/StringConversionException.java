package org.hildan.hashcode.utils.parser.conversion;

import org.hildan.hashcode.utils.parser.InputParsingException;

/**
 * An exception thrown when the {@link StringConverter} cannot convert the given value to the desired type.
 */
public class StringConversionException extends InputParsingException {

    private final Class<?> targetType;

    private final String value;

    StringConversionException(String message) {
        super(message);
        this.targetType = null;
        this.value = null;
    }

    StringConversionException(Class<?> targetType, String value) {
        super("Cannot convert value '" + value + "' to type '" + targetType.getSimpleName() + "'");
        this.targetType = targetType;
        this.value = value;
    }

    StringConversionException(Class<?> targetType, String value, Throwable e) {
        super("Cannot convert value '" + value + "' to type '" + targetType.getSimpleName() + "'", e);
        this.targetType = targetType;
        this.value = value;
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
