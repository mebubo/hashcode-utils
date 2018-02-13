package org.hildan.hashcode.utils.parser;

import org.hildan.hashcode.utils.parser.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface Parser<A> extends Function<Context, A> {
    A parse(Context context) throws InputParsingException;

    default A apply(Context context) {
        return parse(context);
    }

    default <B> Parser<B> map(Function<A, B> f) {
       return ctx -> f.apply(this.parse(ctx));
    }

    static <A> Parser<A> pure(A a) {
        return ctx -> a;
    }

    default <B> Parser<B> flatMap(Function<A, Parser<B>> f) {
        return ctx -> f.apply(this.parse(ctx)).parse(ctx);
    }

    Parser<Integer> integer = Context::readInt;
    Parser<String> string = Context::readString;
    Parser<Double> doubl = Context::readDouble;

    default Parser<List<A>> repeat(Integer n) {
        return ctx -> IntStream.range(0, n).mapToObj(m -> this.parse(ctx)).collect(Collectors.toList());
    }
}
