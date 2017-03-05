package org.hildan.hashcode.utils.parser.readers.section;

import org.hildan.hashcode.utils.parser.InputParsingException;
import org.hildan.hashcode.utils.parser.config.Config;
import org.hildan.hashcode.utils.parser.Context;

public interface SectionReader<P> {

  void readSection(P parent, Context context, Config config) throws InputParsingException;
}
