package org.hildan.hashcode.input.parser.conversion;

import org.hildan.hashcode.input.InputParsingException;

public class TypeConversionException extends InputParsingException {

    TypeConversionException(String message) {
        super(message);
    }

    TypeConversionException(Class<?> targetType, String value) {
        super("Cannot convert value '" + value + "' to type '" + targetType.getSimpleName() + "'");
    }

    TypeConversionException(Class<?> targetType, String value, Throwable e) {
        super("Cannot convert value '" + value + "' to type '" + targetType.getSimpleName() + "'", e);
    }
}
