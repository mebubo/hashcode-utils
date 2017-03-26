package org.hildan.hashcode.utils.parser.readers.creators;

import org.hildan.hashcode.utils.parser.context.Context;

@FunctionalInterface
public interface ObjectCreator<T> {

    T create(Context context);
}
