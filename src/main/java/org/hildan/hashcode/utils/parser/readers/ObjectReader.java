package org.hildan.hashcode.utils.parser.readers;

import org.hildan.hashcode.utils.parser.config.Config;
import org.hildan.hashcode.utils.parser.Context;

public interface ObjectReader<T> {

    T read(Context context, Config config);
}
