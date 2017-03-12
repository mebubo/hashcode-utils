package org.hildan.hashcode.utils.solver;

import java.util.List;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(Theories.class)
public class AbstractFileSolverTest {

    private static class Expectation {
        public final String input;
        public final String expectedOutput;

        private Expectation(String input, String expectedOutput) {
            this.input = input;
            this.expectedOutput = expectedOutput;
        }
    }

    @DataPoints
    public static Expectation[] createDataPoints() {
        return new Expectation[] {
                new Expectation("myInput", "myInput.out"),
                new Expectation("myInput.in", "myInput.out"),
                new Expectation("myInput.in.stuff", "myInput.in.stuff.out"),
                new Expectation("inputs/myInput", "outputs/myInput.out"),
                new Expectation("root/inputs/myInput", "root/outputs/myInput.out"),
                new Expectation("root/inputsweird/myInput", "root/inputsweird/myInput.out"),
                new Expectation("inputs/myInput.in", "outputs/myInput.out"),
                new Expectation("weirdinputs/myInput", "weirdinputs/myInput.out"),
                new Expectation("weirdinputs/myInput.in", "weirdinputs/myInput.out"),
        };
    }

    @Theory
    public void computeOutputFilename(Expectation expectation) {
        AbstractFileSolver solver = new AbstractFileSolver() {
            @Override
            protected List<String> solve(String inputFilename) {
                return null;
            }
        };
        assertEquals(expectation.expectedOutput, solver.computeOutputFilename(expectation.input));
    }
}
