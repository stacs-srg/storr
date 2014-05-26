package uk.ac.standrews.cs.digitising_scotland.population_model.config;

import uk.ac.standrews.cs.digitising_scotland.util.PropertiesWrapper;

import java.util.Properties;

public class PopulationProperties {

    public static final String DATABASE_NAME = "population";
    public static final String PERSON_TABLE_NAME = "Person";
    public static final String PARTNERSHIP_TABLE_NAME = "Partnership";
    public static final String PARTNERSHIP_PARTNER_TABLE_NAME = "Partnership_Partner";
    public static final String PARTNERSHIP_CHILD_TABLE_NAME = "Partnership_Child";

    private static final String DETERMINISTIC_KEY = "deterministic";

    private static final String PROPERTIES_PATH = "population_model/config/config.txt";

    public static Properties getProperties() {

        return PropertiesWrapper.getProperties(PROPERTIES_PATH);
    }

    public static boolean getDeterministic() {

        return Boolean.valueOf((String) getProperties().get(DETERMINISTIC_KEY));
    }
}
