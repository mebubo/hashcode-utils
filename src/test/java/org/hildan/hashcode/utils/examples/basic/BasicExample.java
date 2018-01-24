package org.hildan.hashcode.utils.examples.basic;

import java.util.ArrayList;
import java.util.List;

import org.hildan.hashcode.utils.parser.HCParser;
import org.hildan.hashcode.utils.parser.context.Context;
import org.hildan.hashcode.utils.parser.readers.HCReader;
import org.hildan.hashcode.utils.parser.readers.ObjectReader;
import org.hildan.hashcode.utils.runner.HCRunner;
import org.hildan.hashcode.utils.runner.UncaughtExceptionsPolicy;
import org.hildan.hashcode.utils.solver.HCSolver;

public class BasicExample {

    public static void main(String[] args) {
        ObjectReader<Problem> rootReader = problemReader();
        HCParser<Problem> parser = new HCParser<>(rootReader);
        HCSolver<Problem> solver = new HCSolver<>(parser, Problem::solve);
        HCRunner<String> runner = new HCRunner<>(solver, UncaughtExceptionsPolicy.LOG_ON_SLF4J);
        runner.run(args);
    }

    private static ObjectReader<Problem> problemReader() {
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

class Point {
    private double x;

    private double y;

    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

class Problem {
    private final int nClusters;

    private List<Point> points;

    Problem(int nClusters) {
        this.nClusters = nClusters;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public List<String> solve() {

        // solve the problem here

        // write solution into lines (this is problem-specific)
        List<String> lines = new ArrayList<>();
        lines.add("output line 0");
        lines.add("output line 1");
        return lines;
    }
}
