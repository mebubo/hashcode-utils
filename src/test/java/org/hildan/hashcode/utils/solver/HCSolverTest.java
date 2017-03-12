package org.hildan.hashcode.utils.solver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.hildan.hashcode.utils.parser.HCParser;
import org.hildan.hashcode.utils.parser.readers.TreeObjectReader;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HCSolverTest {

    private static class Input {
        public int num;

        public String[] items;
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
        TreeObjectReader<Input> inputReader = TreeObjectReader.of(Input::new) //
                                                              .fieldsAndVarsLine("num") //
                                                              .stringArrayLine((i, s) -> i.items = s);
        HCParser<Input> parser = new HCParser<>(inputReader);
        solver = new HCSolver<>(parser, i -> Arrays.stream(i.items).map(s -> s + i.num).collect(Collectors.toList()));
    }

    @Test
    public void solve_success() {
        @SuppressWarnings("unchecked")
        List<String> lines = (List<String>) solver.solve(TEST_FILENAME);
        assertEquals(3, lines.size());
        assertEquals("abc42", lines.get(0));
        assertEquals("def42", lines.get(1));
        assertEquals("ghi42", lines.get(2));
    }

    @Test
    public void accept_success() throws IOException {
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
