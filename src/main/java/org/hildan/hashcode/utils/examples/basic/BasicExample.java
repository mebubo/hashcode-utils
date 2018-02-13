package org.hildan.hashcode.utils.examples.basic;

import org.hildan.hashcode.utils.parser.HCParser;
import org.hildan.hashcode.utils.parser.Parser;
import org.hildan.hashcode.utils.runner.HCRunner;
import org.hildan.hashcode.utils.runner.UncaughtExceptionsPolicy;
import org.hildan.hashcode.utils.solver.HCSolver;

public class BasicExample {

    public static void main(String[] args) {
        Parser<Problem> rootReader = BasicParsers.problem();
        HCParser<Problem> parser = new HCParser<>(rootReader);
        HCSolver<Problem> solver = new HCSolver<>(parser, Problem::solve);
        HCRunner<String> runner = new HCRunner<>(solver, UncaughtExceptionsPolicy.LOG_ON_SLF4J);
        runner.run(args);
    }
}

