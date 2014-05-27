package uk.ac.standrews.cs.digitising_scotland.population_model.config;

import uk.ac.standrews.cs.digitising_scotland.util.PropertiesManipulation;

import java.io.IOException;
import java.util.Properties;

public class PopulationProperties {

    public static final String DATABASE_NAME = "population";
    public static final String PERSON_TABLE_NAME = "Person";
    public static final String PARTNERSHIP_TABLE_NAME = "Partnership";
    public static final String PARTNERSHIP_PARTNER_TABLE_NAME = "Partnership_Partner";
    public static final String PARTNERSHIP_CHILD_TABLE_NAME = "Partnership_Child";

    private static final String DETERMINISTIC_KEY = "deterministic";
    private static final String DEFAULT_PROPERTIES_PATH = "config/config.txt";

    private static String properties_path = DEFAULT_PROPERTIES_PATH;

    public static Properties getProperties() throws IOException {

        return PropertiesManipulation.getProperties(properties_path);
    }

    public static void setPropertiesPath(String properties_path) {

        PopulationProperties.properties_path = properties_path;
    }

    public static boolean getDeterministic() throws IOException {

        return Boolean.valueOf((String) getProperties().get(DETERMINISTIC_KEY));
    }
}
