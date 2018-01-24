package org.hildan.hashcode.utils.examples.basic;

import org.hildan.hashcode.utils.parser.HCParser;
import org.hildan.hashcode.utils.parser.context.Context;
import org.hildan.hashcode.utils.parser.readers.HCReader;
import org.hildan.hashcode.utils.parser.readers.ObjectReader;
import org.hildan.hashcode.utils.runner.HCRunner;
import org.hildan.hashcode.utils.runner.UncaughtExceptionsPolicy;
import org.hildan.hashcode.utils.solver.HCSolver;

public class BasicExample {

    public static void main(String[] args) {
        ObjectReader<Problem> rootReader = createProblemReader();
        HCParser<Problem> parser = new HCParser<>(rootReader);
        HCSolver<Problem> solver = new HCSolver<>(parser, Problem::solve);
        HCRunner<String> runner = new HCRunner<>(solver, UncaughtExceptionsPolicy.LOG_ON_SLF4J);
        runner.run(args);
    }

    private static ObjectReader<Problem> createProblemReader() {
        // full custom reader using Context
        ObjectReader<Point> pointReader = (Context ctx) -> {
            double x = ctx.readDouble();
            double y = ctx.readDouble();
            return new Point(x, y);
        };

        // reader using the fluent API
        return HCReader.withVars("P", "C") // reads the 2 first tokens into variables P and C
                       .createFromVar(Problem::new, "C") // creates a new Problem using the value of C as parameter
                       .thenList(Problem::setPoints, "P", pointReader); // reads P elements using the pointReader
    }
}

