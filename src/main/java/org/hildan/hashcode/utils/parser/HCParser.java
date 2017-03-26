package org.hildan.hashcode.utils.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;

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

    private final ObjectReader<T, ?> rootReader;

    /**
     * Creates a new {@code HCParser} with the default configuration.
     *
     * @param rootReader
     *         the reader to use to read the input into an object
     */
    public HCParser(@NotNull ObjectReader<T, ?> rootReader) {
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
    public HCParser(@NotNull ObjectReader<T, ?> rootReader, @RegExp String separator) {
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
    public HCParser(@NotNull ObjectReader<T, ?> rootReader, Config config) {
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
     * @throws FileNotFoundException
     *         if the given file does not exist
     */
    public T parseFile(String filename) throws FileNotFoundException {
        return parse(new FileReader(filename));
    }

    /**
     * Parses the given input to create an instance of T.
     *
     * @param content
     *         the input to parse
     *
     * @return the created object representing the input problem
     */
    public T parse(String content) {
        return parse(new StringReader(content));
    }

    /**
     * Creates an instance of T by reading the input from the given {@link Reader}.
     *
     * @param inputReader
     *         the {@link Reader} to use to consume the input
     *
     * @return the created object representing the input problem
     */
    public T parse(Reader inputReader) {
        return parse(new Context(inputReader, config));
    }

    /**
     * Creates an instance of T by reading the input from the given {@link Context}.
     *
     * @param context
     *         the {@link Context} from which to read the input to parse
     *
     * @return the created object representing the input problem
     */
    public T parse(Context context) {
        T result = rootReader.read(context);
        context.closeReader();
        return result;
    }
}
