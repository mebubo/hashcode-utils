package org.hildan.hashcode.utils.solver;

import java.io.IOException;
import java.util.function.Function;

import org.hildan.hashcode.utils.parser.HCParser;
import org.hildan.hashcode.utils.parser.context.Context;

public class HCSolver<P> extends AbstractFileSolver {

    private final HCParser<P> problemParser;

    private final Function<P, ? extends Iterable<? extends CharSequence>> solver;

    public HCSolver(HCParser<P> problemParser, Function<P, ? extends Iterable<? extends CharSequence>> solver) {
        this.problemParser = problemParser;
        this.solver = solver;
    }

    public static <P extends Solvable> HCSolver<P> of(HCParser<P> parser) {
        return new HCSolver<>(parser, P::solve);
    }

    public static <P extends Solvable> HCSolver<P> of(Function<Context, P> reader) {
        return new HCSolver<>(new HCParser<>(reader), P::solve);
    }

    public static <P> HCSolver<P> of(HCParser<P> parser,
                                     Function<P, ? extends Iterable<? extends CharSequence>> solver) {
        return new HCSolver<>(parser, solver);
    }

    public static <P> HCSolver<P> of(Function<Context, P> reader,
                                     Function<P, ? extends Iterable<? extends CharSequence>> solver) {
        return new HCSolver<>(new HCParser<>(reader), solver);
    }

    @Override
    protected Iterable<? extends CharSequence> solve(String inputFilename) {
        try {
            P problem = problemParser.parseFile(inputFilename);
            return solver.apply(problem);
        } catch (IOException e) {
            throw new SolverException("Exception occurred while parsing the input file '" + inputFilename + "'", e);
        }
    }
}
