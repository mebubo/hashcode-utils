package org.hildan.hashcode.solver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.hildan.hashcode.parser.HCParser;
import org.hildan.hashcode.parser.readers.TreeObjectReader;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HCSolverTest {

    private static class Input {
        public int num;

        public String[] elements;
    }

    private static final String TEST_FILENAME = "testfile.in";

    private static final String EXPECTED_OUTPUT_FILENAME = "testfile.out";

    private static final String fileContent = "42\nabc def ghi";

    private HCSolver<Input> solver;

    @BeforeClass
    public static void createTestInputFile() throws IOException {
        Path path = Paths.get(TEST_FILENAME);
        Files.write(path, fileContent.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Before
    public void setUp() throws IOException {
        TreeObjectReader<Input> inputReader = TreeObjectReader.of(Input::new)
                                                              .addFieldsLine("num")
                                                              .addArrayLine((i, s) -> i.elements = s, String[]::new,
                                                                      s -> s);
        HCParser<Input> parser = new HCParser<>(inputReader);
        solver = HCSolver.from(parser, i -> Arrays.stream(i.elements).map(s -> s + i.num).collect(Collectors.toList()));
    }

    @Test
    public void solve_success() {
        Input input = new Input();
        input.num = 5;
        input.elements = new String[]{"A", "B", "C"};
        @SuppressWarnings("unchecked")
        List<String> lines = (List<String>) solver.solve(input);
        assertEquals(3, lines.size());
        assertEquals("A5", lines.get(0));
        assertEquals("B5", lines.get(1));
        assertEquals("C5", lines.get(2));
    }

    @Test
    public void fullTest() throws IOException {
        solver.accept(TEST_FILENAME);
        Path outputFilePath = Paths.get(EXPECTED_OUTPUT_FILENAME);
        assertTrue(Files.exists(outputFilePath));
        List<String> lines = Files.readAllLines(outputFilePath);
        assertEquals(3, lines.size());
        assertEquals("abc42", lines.get(0));
        assertEquals("def42", lines.get(1));
        assertEquals("ghi42", lines.get(2));
        Files.delete(Paths.get(EXPECTED_OUTPUT_FILENAME));
    }

    @AfterClass
    public static void deleteTestInputFile() throws IOException {
        Files.delete(Paths.get(TEST_FILENAME));
    }
}