package uk.ac.standrews.cs.digitising_scotland.population_model.config;

import uk.ac.standrews.cs.digitising_scotland.util.PropertiesManipulation;

import java.io.IOException;
import java.util.Properties;

public class PopulationProperties {

    public static final String DATABASE_NAME = "population";

    public static final String PERSON_TABLE_NAME = "Person";
    public static final String PARTNERSHIP_TABLE_NAME = "Partnership";
    public static final String PARTNERSHIP_PARTNER_TABLE_NAME = "PartnershipPartner";
    public static final String PARTNERSHIP_CHILD_TABLE_NAME = "PartnershipChild";

    public static final String PERSON_FIELD_ID = "id";
    public static final String PERSON_FIELD_GENDER = "gender";
    public static final String PERSON_FIELD_NAME = "name";
    public static final String PERSON_FIELD_SURNAME = "surname";
    public static final String PERSON_FIELD_BIRTH_DATE = "birth_date";
    public static final String PERSON_FIELD_DEATH_DATE = "death_date";
    public static final String PERSON_FIELD_OCCUPATION = "occupation";
    public static final String PERSON_FIELD_CAUSE_OF_DEATH = "cause_of_death";
    public static final String PERSON_FIELD_ADDRESS = "address";

    public static final String PARTNERSHIP_FIELD_ID = "id";
    public static final String PARTNERSHIP_FIELD_DATE = "date";

    public static final String PARTNERSHIP_FIELD_PERSON_ID = "person_id";
    public static final String PARTNERSHIP_FIELD_PARTNERSHIP_ID = "partnership_id";

    private static final String DETERMINISTIC_KEY = "deterministic";
    private static final String DEFAULT_PROPERTIES_PATH = "config/config.txt";

    private static String properties_path = DEFAULT_PROPERTIES_PATH;

    public static Properties getProperties() throws IOException {

        return PropertiesManipulation.getProperties(properties_path);
    }

    public static void setPropertiesPath(final String properties_path) {

        PopulationProperties.properties_path = properties_path;
    }

    public static boolean getDeterministic() throws IOException {

        return Boolean.valueOf((String) getProperties().get(DETERMINISTIC_KEY));
    }
}
