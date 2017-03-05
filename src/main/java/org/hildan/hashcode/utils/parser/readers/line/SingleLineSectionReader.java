package org.hildan.hashcode.utils.parser.readers.line;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.config.Config;
import org.hildan.hashcode.utils.parser.Context;
import org.hildan.hashcode.utils.parser.readers.section.SectionReader;

public abstract class SingleLineSectionReader<P> implements SectionReader<P> {

    @Override
    public void readSection(P parent, Context context, Config config) throws InputParsingException {
        int lineNum = context.getNextLineNumber();
        String line = context.readLine();
        String[] values = line.split(config.getSeparator());
        try {
            setValues(parent, values, context, config);
        } catch (Exception e) {
            throw new InputParsingException(lineNum, line, e.getMessage(), e);
        }
    }

    protected abstract void setValues(P objectToFill, String[] values, Context context, Config config) throws Exception;
}
