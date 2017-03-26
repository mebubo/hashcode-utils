package org.hildan.hashcode.utils.parser.readers.builder;

import org.hildan.hashcode.utils.parser.context.Context;

public class VariableReader implements StateReader {

    private final String[] variableNames;

    public VariableReader(String... variableNames) {
        this.variableNames = variableNames;
    }

    @Override
    public void read(Context context) {
        for (String var : variableNames) {
            context.setVariable(var, context.readString());
        }
    }
}
