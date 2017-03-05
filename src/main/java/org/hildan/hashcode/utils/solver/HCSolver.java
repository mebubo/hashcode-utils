package org.hildan.hashcode.utils.solver;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import org.hildan.hashcode.utils.parser.HCParser;

public abstract class HCSolver<P> extends AbstractFileSolver {

    private final HCParser<P> problemParser;

    protected HCSolver(HCParser<P> problemParser) {
        this.problemParser = problemParser;
    }

    public static <P> HCSolver<P> from(HCParser<P> problemParser, Function<P, List<String>> solver) {
        return new HCSolver<P>(problemParser) {
            @Override
            protected List<String> solve(P problem) {
                return solver.apply(problem);
            }
        };
    }

    @Override
    protected Iterable<? extends CharSequence> solve(String inputFilename) {
        try {
            P problem = problemParser.parse(inputFilename);
            return solve(problem);
        } catch (IOException e) {
            throw new SolverException("Exception occurred while parsing the input file '" + inputFilename + "'", e);
        }
    }

    protected abstract Iterable<? extends CharSequence> solve(P problem);
}
