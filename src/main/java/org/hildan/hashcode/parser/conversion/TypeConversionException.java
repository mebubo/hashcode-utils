package org.hildan.hashcode.parser.conversion;

import org.hildan.hashcode.parser.InputParsingException;

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
