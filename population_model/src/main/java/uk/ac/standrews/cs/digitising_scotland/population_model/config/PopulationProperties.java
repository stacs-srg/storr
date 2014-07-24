/**
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.population_model.config;

import uk.ac.standrews.cs.digitising_scotland.util.PropertiesManipulation;

import java.io.IOException;
import java.util.Properties;

/**
 * Defines various population properties.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class PopulationProperties {

    /**
     * The default database address.
     */
    public static final String DEFAULT_DB_ADDRESS = "localhost";

    /**
     * The default database username.
     */
    public static final String DEFAULT_DB_USERNAME = "ds_user";

    /**
     * The default database password.
     */
    public static final String DEFAULT_DB_PASSWORD = "";

    /**
     * The default database name.
     */
    public static final String DEFAULT_DATABASE_NAME = "population";

    /**
     * The name of the table storing people.
     */
    public static final String PERSON_TABLE_NAME = "Person";

    /**
     * The name of the table storing partnerships.
     */
    public static final String PARTNERSHIP_TABLE_NAME = "Partnership";

    /**
     * The name of the table linking people and partnerships.
     */
    public static final String PARTNERSHIP_PARTNER_TABLE_NAME = "PartnershipPartner";

    /**
     * The name of the table linking partnerships and child_ids.
     */
    public static final String PARTNERSHIP_CHILD_TABLE_NAME = "PartnershipChild";

    /**
     * The name of the column in the people table storing identifiers of people.
     */
    public static final String PERSON_FIELD_ID = "id";

    /**
     * The name of the column in the people table storing sex.
     */
    public static final String PERSON_FIELD_GENDER = "gender";

    /**
     * The name of the column in the people table storing first name.
     */
    public static final String PERSON_FIELD_NAME = "name";

    /**
     * The name of the column in the people table storing surname.
     */
    public static final String PERSON_FIELD_SURNAME = "surname";

    /**
     * The name of the column in the people table storing date of birth.
     */
    public static final String PERSON_FIELD_BIRTH_DATE = "birth_date";

    /**
     * The name of the column in the people table storing address.
     */
    public static final String PERSON_FIELD_BIRTH_PLACE = "birth_place";

    /**
     * The name of the column in the people table storing date of death.
     */
    public static final String PERSON_FIELD_DEATH_DATE = "death_date";

    /**
     * The name of the column in the people table storing address.
     */
    public static final String PERSON_FIELD_DEATH_PLACE = "death_place";

    /**
     * The name of the column in the people table storing cause of death.
     */
    public static final String PERSON_FIELD_DEATH_CAUSE = "death_cause";

    /**
     * The name of the column in the people table storing occupation.
     */
    public static final String PERSON_FIELD_OCCUPATION = "occupation";

    /**
     * The name of the column in the partnerships table storing partnership identifiers.
     */
    public static final String PARTNERSHIP_FIELD_ID = "id";

    /**
     * The name of the column in the partnerships table storing marriage dates.
     */
    public static final String PARTNERSHIP_FIELD_DATE = "marriage_date";

    /**
     * The name of the column in the partnerships table storing marriage dates.
     */
    public static final String PARTNERSHIP_FIELD_PLACE = "marriage_place";

    /**
     * The name of the column in the people/partnerships table storing person identifiers.
     */
    public static final String PARTNERSHIP_FIELD_PERSON_ID = "person_id";

    /**
     * The name of the column in the people/partnerships table storing partnership identifiers.
     */
    public static final String PARTNERSHIP_FIELD_PARTNERSHIP_ID = "partnership_id";

    private static final String DETERMINISTIC_KEY = "deterministic";
    private static final String DEFAULT_PROPERTIES_PATH = "config/config.txt";

    private static String properties_path = DEFAULT_PROPERTIES_PATH;
    private static String database_name = DEFAULT_DATABASE_NAME;

    /**
     * Gets the properties currently in use.
     *
     * @return the current properties
     * @throws IOException if the properties cannot be accessed
     */
    public static Properties getProperties() throws IOException {

        return PropertiesManipulation.getProperties(properties_path);
    }

    /**
     * Sets the file path of the properties to be used.
     * @param properties_path the file path
     */
    @SuppressWarnings("UnusedDeclaration")
    public static void setPropertiesPath(final String properties_path) {

        PopulationProperties.properties_path = properties_path;
    }

    /**
     * Gets the name of the database.
     * @return the name of the database
     */
    public static String getDatabaseName() {

        return database_name;
    }

    /**
     * Sets the name of the database.
     * @param database_name the name of the database
     */
    public static void setDatabaseName(final String database_name) {

        PopulationProperties.database_name = database_name;
    }

    /**
     * Checks whether pseudo-random elements are generated deterministically.
     * @return true if elements are generated deterministically
     * @throws IOException if the check cannot be completed
     */
    public static boolean isDeterministic() throws IOException {

        return Boolean.valueOf((String) getProperties().get(DETERMINISTIC_KEY));
    }
}
