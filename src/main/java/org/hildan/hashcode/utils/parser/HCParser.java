package org.hildan.hashcode.utils.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.hildan.hashcode.utils.parser.config.Config;
import org.hildan.hashcode.utils.parser.context.Context;
import org.hildan.hashcode.utils.parser.readers.ObjectReader;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

/**
 * {@code HCParser} parses textual input using a root {@link ObjectReader}. This is useful to convert Hash Code's
 * input file into an object representation of the problem.
 *
 * @param <T>
 *         the type of objects this parser yields
 */
public class HCParser<T> {

    private final Config config;

    private final ObjectReader<T> rootReader;

    /**
     * Creates a new {@code HCParser} with the default configuration.
     *
     * @param rootReader
     *         the reader to use to read the input into an object
     */
    public HCParser(@NotNull ObjectReader<T> rootReader) {
        this(rootReader, new Config());
    }

    /**
     * Creates a new {@code HCParser} using the given separator.
     *
     * @param rootReader
     *         the reader to use to read the input into an object
     * @param separator
     *         the separator between elements within an input line
     */
    public HCParser(@NotNull ObjectReader<T> rootReader, @RegExp String separator) {
        config = new Config(separator);
        this.rootReader = rootReader;
    }

    /**
     * Creates a new {@code HCParser} with the given configuration.
     *
     * @param rootReader
     *         the reader to use to read the input into an object
     * @param config
     *         the configuration defining this parser's behaviour
     */
    public HCParser(@NotNull ObjectReader<T> rootReader, Config config) {
        this.config = config;
        this.rootReader = rootReader;
    }

    /**
     * Parses the given file to create an instance of T.
     *
     * @param filename
     *         the path to the file to parse
     *
     * @return the created object representing the input problem
     * @throws IOException
     *         if an error occurs while reading the file
     */
    public T parse(String filename) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filename));
        return parse(lines);
    }

    /**
     * Parses the given input lines to create an instance of T.
     *
     * @param lines
     *         the input lines to parse
     *
     * @return the created object representing the input problem
     */
    public T parse(List<String> lines) {
        Context context = new Context(lines);
        return parse(context);
    }

    /**
     * Creates an instance of T by reading the input from the given context.
     *
     * @param context
     *         the context from which to read the input to parse
     *
     * @return the created object representing the input problem
     */
    public T parse(Context context) {
        return rootReader.read(context, config);
    }
}
