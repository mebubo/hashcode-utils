package org.hildan.hashcode.input.parser.readers;

import org.hildan.hashcode.input.config.Config;
import org.hildan.hashcode.input.parser.Context;

public interface ObjectReader<T> {

    T read(Context context, Config config);
}
