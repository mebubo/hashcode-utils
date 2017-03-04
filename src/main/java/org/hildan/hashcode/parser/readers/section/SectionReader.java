package org.hildan.hashcode.parser.readers.section;

import org.hildan.hashcode.parser.InputParsingException;
import org.hildan.hashcode.parser.config.Config;
import org.hildan.hashcode.parser.Context;

public interface SectionReader<P> {

  void readSection(P parent, Context context, Config config) throws InputParsingException;
}
