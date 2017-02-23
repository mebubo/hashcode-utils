package org.hildan.hashcode.input.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.hildan.hashcode.input.config.Config;
import org.hildan.hashcode.input.parser.readers.ObjectReader;

public class HCParser<T> {

    private final Config config;
    private final ObjectReader<T> rootReader;

    public HCParser(ObjectReader<T> rootReader) {
        this(new Config(), rootReader);
    }

    public HCParser(Config config, ObjectReader<T> rootReader) {
        this.config = config;
        this.rootReader = rootReader;
    }

    public T parse(String filename) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filename));
        return parse(lines);
    }

    public T parse(List<String> lines) {
        Context context = new Context(lines);
        return rootReader.read(context, config);
    }
}
