package uk.ac.standrews.cs.digitising_scotland.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesManipulation {

    public static synchronized Properties getProperties(final String properties_path_string) throws IOException {

        try (InputStream stream = PropertiesManipulation.class.getClassLoader().getResourceAsStream(properties_path_string)) {

            Properties properties = new Properties();
            properties.load(stream);
            return properties;
        }
    }
}
