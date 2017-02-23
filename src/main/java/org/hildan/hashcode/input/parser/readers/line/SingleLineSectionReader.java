package org.hildan.hashcode.input.parser.readers.line;

import org.hildan.hashcode.input.InputParsingException;
import org.hildan.hashcode.input.config.Config;
import org.hildan.hashcode.input.parser.Context;
import org.hildan.hashcode.input.parser.readers.section.SectionReader;

public abstract class SingleLineSectionReader<P> implements SectionReader<P> {

    @Override
    public void readSection(P objectToFill, Context context, Config config) throws InputParsingException {
        int lineNum = context.getNextLineNumber();
        String line = context.readLine();
        String[] values = line.split(config.getSeparator());
        try {
            setValues(objectToFill, values, config);
        } catch (Exception e) {
            throw new InputParsingException(lineNum, line, e.getMessage(), e);
        }
    }

    protected abstract void setValues(P objectToFill, String[] values, Config config) throws Exception;
}
