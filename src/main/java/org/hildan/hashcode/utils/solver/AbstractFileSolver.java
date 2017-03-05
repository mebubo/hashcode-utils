package org.hildan.hashcode.utils.solver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.function.Consumer;

public abstract class AbstractFileSolver implements Consumer<String> {

    private static final String INPUT_EXTENSION = ".in";

    private static final String OUTPUT_EXTENSION = ".out";

    private static final String INPUT_FOLDER = "inputs/";

    private static final String OUTPUT_FOLDER = "outputs/";

    @Override
    public void accept(String inputFilename) {
        Iterable<? extends CharSequence> lines = solve(inputFilename);
        String outputFile = computeOutputFilename(inputFilename);
        try {
            writeOutputFile(outputFile, lines);
        } catch (IOException e) {
            throw new SolverException("Exception occurred while writing to the output file '" + outputFile + "'", e);
        }
    }

    protected abstract Iterable<? extends CharSequence> solve(String inputFilename);

    protected String computeOutputFilename(String inputFilename) {
        String outputFilename = inputFilename;
        outputFilename = outputFilename.replaceAll("^" + INPUT_FOLDER, OUTPUT_FOLDER);
        outputFilename = outputFilename.replaceAll("/" + INPUT_FOLDER, "/" + OUTPUT_FOLDER);
        if (outputFilename.endsWith(INPUT_EXTENSION)) {
            outputFilename = outputFilename.replaceAll(INPUT_EXTENSION + "$", OUTPUT_EXTENSION);
        } else {
            outputFilename = outputFilename + ".out";
        }
        return outputFilename;
    }

    protected void writeOutputFile(String outputFilename, Iterable<? extends CharSequence> lines) throws IOException {
        Path filePath = Paths.get(outputFilename);
        Path parentDir = filePath.getParent();
        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }
        Files.write(filePath, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }
}
