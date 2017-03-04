package org.hildan.hashcode.parser.readers;

import org.hildan.hashcode.parser.config.Config;
import org.hildan.hashcode.parser.Context;

public interface ObjectReader<T> {

    T read(Context context, Config config);
}
