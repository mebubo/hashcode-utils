package org.hildan.hashcode.utils.solver;

import java.io.IOException;
import java.util.function.Function;

import org.hildan.hashcode.utils.parser.HCParser;

public class HCSolver<P> extends AbstractFileSolver {

    private final HCParser<P> problemParser;

    private final Function<P, ? extends Iterable<? extends CharSequence>> solver;

    public HCSolver(HCParser<P> problemParser, Function<P, ? extends Iterable<? extends CharSequence>> solver) {
        this.problemParser = problemParser;
        this.solver = solver;
    }

    @Override
    protected Iterable<? extends CharSequence> solve(String inputFilename) {
        try {
            P problem = problemParser.parse(inputFilename);
            return solver.apply(problem);
        } catch (IOException e) {
            throw new SolverException("Exception occurred while parsing the input file '" + inputFilename + "'", e);
        }
    }
}
