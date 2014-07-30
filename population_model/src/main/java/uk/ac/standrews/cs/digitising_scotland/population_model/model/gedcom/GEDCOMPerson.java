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
package uk.ac.standrews.cs.digitising_scotland.population_model.model.gedcom;

import org.gedcom4j.model.FamilyChild;
import org.gedcom4j.model.Individual;
import org.gedcom4j.model.IndividualAttribute;
import org.gedcom4j.model.IndividualAttributeType;
import org.gedcom4j.model.IndividualEvent;
import org.gedcom4j.model.PersonalName;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.AbstractPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.text.ParseException;
import java.util.List;

/**
 * Person implementation for a population represented in a GEDCOM file.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk>
 */
public class GEDCOMPerson extends AbstractPerson {

    private static final String MALE_STRING = String.valueOf(IPerson.MALE);

    /**
     * Initialises the partnership.
     *
     * @param individual the GEDCOM person representation
     * @throws ParseException if the birth or death date is incorrectly formatted
     */
    public GEDCOMPerson(final Individual individual) throws ParseException {

        setId(individual);
        setSex(individual);
        setNames(individual);
        setParents(individual);
        setEvents(individual);
        setOccupation(individual);
    }

    private void setId(final Individual individual) {

        id = GEDCOMPopulationWriter.idToInt(individual.xref);
    }

    private void setSex(final Individual individual) {

        sex = individual.sex.toString().equals(MALE_STRING) ? IPerson.MALE : IPerson.FEMALE;
    }

    private void setNames(final Individual individual) {

        final List<PersonalName> names = individual.names;

        first_name = findFirstNames(names);
        surname = findSurname(names);
    }

    private void setParents(final Individual individual) {

        final List<FamilyChild> families = individual.familiesWhereChild;
        parents_partnership_id = !families.isEmpty() ? GEDCOMPopulationWriter.idToInt(families.get(0).family.xref) : -1;
    }

    private void setEvents(final Individual individual) throws ParseException {

        for (final IndividualEvent event : individual.events) {

            switch (event.type) {

                case BIRTH:
                    birth_date = DateManipulation.parseDate(event.date.toString());
                    birth_place = event.place.placeName;
                    break;

                case DEATH:
                    death_date = DateManipulation.parseDate(event.date.toString());
                    death_place = event.place.placeName;
                    death_cause = event.cause.toString();
                    break;

                default:
                    break;
            }
        }
    }

    private void setOccupation(final Individual individual) {

        final List<IndividualAttribute> occupation_attributes = individual.getAttributesOfType(IndividualAttributeType.OCCUPATION);
        if (!occupation_attributes.isEmpty()) {
            occupation = occupation_attributes.get(0).description.toString();
        }
    }

    private static String findSurname(final List<PersonalName> names) {

        for (final PersonalName gedcom_name : names) {

            final String name = gedcom_name.toString();
            if (name.contains("/")) { // Slashes denote surname
                final int start = name.indexOf('/');
                final int end = name.lastIndexOf('/');
                if (end > start) {
                    return name.substring(start + 1, end);
                }
            }
        }
        return null;
    }

    private static String findFirstNames(final List<PersonalName> names) {

        final StringBuilder builder = new StringBuilder();

        for (final PersonalName gedcom_name : names) {

            if (builder.length() > 0) {
                builder.append(' ');
            }

            String name = gedcom_name.toString();
            if (name.contains("/")) { // Slashes denote surname
                final int start = name.indexOf('/');
                final int end = name.lastIndexOf('/');
                if (end > start) {
                    name = name.substring(0, start) + name.substring(end + 1, name.length());
                }
            }
            builder.append(name);
        }
        return builder.toString();
    }
}
