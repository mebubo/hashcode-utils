package org.hildan.hashcode.utils.parser.context;

import org.hildan.hashcode.utils.parser.InputParsingException;

/**
 * Thrown when a context variable is accessed but has never been set.
 */
public class UndefinedVariableException extends InputParsingException {

    public UndefinedVariableException(String variableName) {
        super("The variable '" + variableName + "' was not defined in this context");
    }
}
