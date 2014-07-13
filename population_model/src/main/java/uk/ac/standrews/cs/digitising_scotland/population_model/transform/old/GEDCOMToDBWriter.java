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
package uk.ac.standrews.cs.digitising_scotland.population_model.transform.old;

import org.gedcom4j.model.Family;
import org.gedcom4j.model.FamilyEvent;
import org.gedcom4j.model.FamilyEventType;
import org.gedcom4j.model.Individual;
import org.gedcom4j.parser.GedcomParser;
import org.gedcom4j.parser.GedcomParserException;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.database.DBConnector;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.old.Person;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.old.PersonFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Reads in a GEDCOM file and populates a database.
 *
 * GEDCOM specification: http://homepages.rootsweb.ancestry.com/~pmcbride/gedcom/55gctoc.htm
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class GEDCOMToDBWriter implements AutoCloseable {

    private final Connection connection;
    private final GedcomParser parser;
    private final PersonFactory person_factory;

    public GEDCOMToDBWriter(final String path_string) throws IOException, GedcomParserException, SQLException {

        connection = new DBConnector(PopulationProperties.getDatabaseName()).createConnection();
        person_factory = new PersonFactory();

        parser = new GedcomParser();
        parser.load(path_string);
    }

    public int importPeople() throws ParseException, SQLException {

        final Collection<Individual> individuals = parser.gedcom.individuals.values();
        for (final Individual individual : individuals) {

            importPerson(person_factory.createPerson(individual));
        }
        return individuals.size();
    }

    public int importFamilies() throws ParseException, SQLException {

        final Collection<Family> families = parser.gedcom.families.values();
        for (final Family family : families) {

            importFamily(family);
        }
        return families.size();
    }

    public void close() throws SQLException {

        connection.close();
    }

    private void importFamily(final Family family) throws ParseException, SQLException {

        final int familyXref = Integer.valueOf(stripAtSymbols(family.xref));
        final int husbandXref = Integer.valueOf(stripAtSymbols(family.husband.xref));
        final int wifeXref = Integer.valueOf(stripAtSymbols(family.wife.xref));

        String marriage_date_string = "";
        for (final FamilyEvent event : family.events) {
            if (event.type == FamilyEventType.MARRIAGE) {
                marriage_date_string = event.date.toString();
            }
        }

        final Date marriage_date = DateManipulation.stringSQLToDate(marriage_date_string);

        final List<Integer> childXrefs = new ArrayList<Integer>();
        for (final Individual child : family.children) {
            childXrefs.add(Integer.valueOf(stripAtSymbols(child.xref)));
        }

        importFamily(familyXref, marriage_date, husbandXref, wifeXref, childXrefs);
    }

    private void importPerson(final Person fp) throws SQLException {

        try (final PreparedStatement statement = connection.prepareStatement("INSERT INTO " + PopulationProperties.PERSON_TABLE_NAME + " VALUES( ?,?,?,?,?,?,?,?,? );")) {

            int pos = 1;

            statement.setInt(pos++, fp.getId());
            statement.setString(pos++, String.valueOf(fp.getSex()));
            statement.setString(pos++, fp.getFirstName());
            statement.setString(pos++, fp.getSurname());
            statement.setDate(pos++, fp.getBirthDate());
            statement.setDate(pos++, (Date) fp.getDeathDate());
            statement.setString(pos++, fp.getOccupation());
            statement.setString(pos++, fp.getCauseOfDeath());
            statement.setString(pos, fp.getAddress());

            statement.executeUpdate();
        }
    }

    private void importFamily(final int family_id, final Date marriage_date, final int husband_id, final int wife_id, final List<Integer> child_ids) throws SQLException {

        insertPartnership(family_id, marriage_date);
        insertPartner(family_id, husband_id);
        insertPartner(family_id, wife_id);

        for (final Integer childID : child_ids) {
            insertChild(family_id, childID);
        }
    }

    private void insertPartnership(final int family_id, final Date marriage_date) throws SQLException {

        // TODO factor out prepared statement
        try (final PreparedStatement statement = connection.prepareStatement("INSERT INTO " + PopulationProperties.PARTNERSHIP_TABLE_NAME + " VALUES( ?,? );")) {

            int pos = 1;

            statement.setInt(pos++, family_id);
            statement.setDate(pos, marriage_date);

            statement.executeUpdate();
        }
    }

    private void insertPartner(final int family_id, final int partner_id) throws SQLException {

        // TODO factor out prepared statement
        try (final PreparedStatement statement = connection.prepareStatement("INSERT INTO " + PopulationProperties.PARTNERSHIP_PARTNER_TABLE_NAME + " VALUES( ?,? );")) {

            int pos = 1;

            statement.setInt(pos++, partner_id);
            statement.setInt(pos, family_id);

            statement.executeUpdate();
        }
    }

    private void insertChild(final int family_id, final int child_id) throws SQLException {

        // TODO factor out prepared statement
        try (final PreparedStatement statement = connection.prepareStatement("INSERT INTO " + PopulationProperties.PARTNERSHIP_CHILD_TABLE_NAME + " VALUES( ?,? );")) {

            int pos = 1;

            statement.setInt(pos++, child_id);
            statement.setInt(pos, family_id);

            statement.executeUpdate();
        }
    }

    private String stripAtSymbols(final String reference) {

        return reference.substring(1, reference.length() - 1);
    }
}
