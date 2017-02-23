package org.hildan.hashcode.input.parser.readers.section;

import org.hildan.hashcode.input.InputParsingException;
import org.hildan.hashcode.input.config.Config;
import org.hildan.hashcode.input.parser.Context;

public interface SectionReader<P> {

  void readSection(P parent, Context context, Config config) throws InputParsingException;
}
