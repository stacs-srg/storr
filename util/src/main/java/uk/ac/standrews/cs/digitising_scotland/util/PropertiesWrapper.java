package uk.ac.standrews.cs.digitising_scotland.util;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Wrapper for Java wrapper class, with automatic saving on property update.
 * 
 * @author Angus Macdonald (angus AT cs.st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class PropertiesWrapper extends Properties {

    private final Path properties_path;
    private static final Charset CHARSET = Charset.forName("UTF-8");

    private static final String POPULATION_PROPERTIES_PATH = "config/config.txt";

    private static PropertiesWrapper wrapper = null;

    public static synchronized Properties getProperties() {

        if (wrapper == null) {
            wrapper = new PropertiesWrapper(Paths.get(POPULATION_PROPERTIES_PATH));
        }
        return wrapper;
    }

    /**
     * Sets the property for a given key, and saves to the backing file.
     *
     * @param key a key
     * @param value the new value to be associated with the key
     */
    public Object setProperty(final String key, final String value) {

        Object previous = super.setProperty(key, value);
        try {
            save();
        }
        catch (IOException e) {
            throw new RuntimeException("could not save to wrapper file: " + properties_path.toAbsolutePath(), e);
        }
        return previous;
    }

    /**
     * Creates a wrapper wrapper for the given file path.
     * 
     * @param properties_path the file to contain the wrapper
     * @throws RuntimeException if the file could not be created or the wrapper could not be loaded from it
     */
    private PropertiesWrapper(final Path properties_path) {

        this.properties_path = properties_path;

        try {
            createFileIfNecessary(properties_path);
            loadProperties();

        } catch (IOException e) {
            throw new RuntimeException("could not save to wrapper file: " + properties_path.toAbsolutePath(), e);
        }
    }

    private void createFileIfNecessary(final Path properties_path) throws IOException {

        if (!Files.exists(properties_path)) {

            Path parent_dir = properties_path.getParent();
            if (parent_dir != null) Files.createDirectories(parent_dir);

            Files.createFile(properties_path);
        }
    }

    private void loadProperties() throws IOException {

        try (Reader reader = Files.newBufferedReader(properties_path, CHARSET)) {
            load(reader);
        }
    }

    private void save() throws IOException {

        try (Writer writer = Files.newBufferedWriter(properties_path, CHARSET)) {
            store(writer, "Properties File");
        }
    }
}
