package uk.ac.standrews.cs.digitising_scotland.population_model.model;

import org.gedcom4j.model.Individual;
import org.gedcom4j.model.IndividualEvent;
import org.gedcom4j.model.IndividualEventType;
import org.gedcom4j.model.PersonalName;
import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.FileBasedEnumeratedDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.InconsistentWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.util.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.population_model.transform.PopulationToDB;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Random;

public class PersonFactory {

    private FileBasedEnumeratedDistribution occupations;
    private FileBasedEnumeratedDistribution causes_of_death;

    public PersonFactory() {

        Random random = RandomFactory.getRandom();

        try {
            final String occupation_file_name = PopulationProperties.getProperties().getProperty(PopulationToDB.OCCUPATION_DISTRIBUTION_KEY);
            final String cod_file_name = PopulationProperties.getProperties().getProperty(PopulationToDB.CAUSE_OF_DEATH_DISTRIBUTION_KEY);

            occupations = new FileBasedEnumeratedDistribution(occupation_file_name, random);
            causes_of_death = new FileBasedEnumeratedDistribution(cod_file_name, random);
        }
        catch (final IOException e) {
            ErrorHandling.exceptionError(e, "Error reading distribution file");
        } catch (InconsistentWeightException e) {
            ErrorHandling.exceptionError(e, "Error in cumulative weights in file");
        }
    }

    public Person createPerson(final CompactPerson compact_person) {

        // TODO investigate usage of this method and the COD and occupation fields.

        return new Person(compact_person.getId(), compact_person.isMale() ? Person.MALE : Person.FEMALE, DateManipulation.daysToSQLDate(compact_person.getDateOfBirth()),
                DateManipulation.daysToSQLDate(compact_person.getDateOfDeath()), "Occupation", "Cause of death", "Address");
    }

    public Person createPerson(final Individual gedcom_person) throws ParseException {

        // TODO - this method should not be here - leaks information from Gedcom into this package.
        // Need to refactor out the surname creation and move this method into another place.

        final Person person = new Person();

        final String id = stripAtSymbols(gedcom_person.xref);

        try {
            person.setID(Integer.parseInt(id));
        }
        catch (final NumberFormatException e) {
            throw new ParseException(e.getMessage(), 0);
        }

        person.setGender(gedcom_person.sex.toString().equals(Person.MALE_STRING) ? Person.MALE : Person.FEMALE);

        final List<PersonalName> names = gedcom_person.names;

        person.setSurname(findSurname(names));
        person.setFirstName(appendFirstNames(names));

        final List<IndividualEvent> events = gedcom_person.events;
        for (final IndividualEvent event : events) {
            if (event.type.equals(IndividualEventType.BIRTH)) { // TODO Run out of time - need to sort out types!
                person.setBirthDate(DateManipulation.stringSQLToDate(event.date.toString()));
            }
            if (event.type.equals(IndividualEventType.DEATH)) {
                person.setDeathDate(DateManipulation.stringSQLToDate(event.date.toString()));
            }
        }

        person.setOccupation(occupations.getSample());

        if (person.getDeathDate() != null) {
            person.setCauseOfDeath(causes_of_death.getSample());
        }
        person.setAddress("Address");

        return person;
    }

    private static String findSurname(final List<PersonalName> names) {

        for (final PersonalName gedcom_name : names) {

            final String name = gedcom_name.toString();
            if (name.contains("/")) { // Slashes denote surname
                final int start = name.indexOf('/');
                final int end = name.lastIndexOf('/');
                if (end > start) { return name.substring(start + 1, end); }
            }
        }
        return "not found";
    }

    private static String appendFirstNames(final List<PersonalName> names) {

        final StringBuilder builder = new StringBuilder();

        for (final PersonalName gedcom_name : names) {
            if (builder.length() > 0) { // Make concatenation of names nice
                builder.append(" ");
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

    private static String stripAtSymbols(final String reference) {

        return reference.substring(1, reference.length() - 1);
    }
}
