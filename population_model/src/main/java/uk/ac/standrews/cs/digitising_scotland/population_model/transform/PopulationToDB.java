package uk.ac.standrews.cs.digitising_scotland.population_model.transform;

import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;
import uk.ac.standrews.cs.digitising_scotland.population_model.database.DBConnector;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.FemaleFirstNameDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.FileBasedEnumeratedDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.InconsistentWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.MaleFirstNameDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.SurnameDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.util.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IDFactory;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;
import uk.ac.standrews.cs.digitising_scotland.util.ProgressIndicator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Writes a representation of the population to DB.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
public class PopulationToDB implements AutoCloseable {

    public static final String OCCUPATION_DISTRIBUTION_KEY = "occupation_distribution_filename";
    public static final String CAUSE_OF_DEATH_DISTRIBUTION_KEY = "cause_of_death_distribution_filename";

    private final MaleFirstNameDistribution male_first_name_distribution;
    private final FemaleFirstNameDistribution female_first_name_distribution;
    private final SurnameDistribution surname_distribution;
    private final FileBasedEnumeratedDistribution occupation_distribution;
    private final FileBasedEnumeratedDistribution cause_of_death_distribution;

    private final Connection connection;
    private final CompactPopulation population;
    private final ProgressIndicator progress_indicator;

    /**
     *
     * @param population  the population
     * @throws java.io.FileNotFoundException if the file does not exist and cannot be created
     */
    public PopulationToDB(final CompactPopulation population, final ProgressIndicator progress_indicator) throws IOException, InconsistentWeightException, SQLException {

        this.population = population;
        this.progress_indicator = progress_indicator;

        connection = new DBConnector(PopulationProperties.DATABASE_NAME).createConnection();

        final String occupation_distribution_file_name = PopulationProperties.getProperties().getProperty(OCCUPATION_DISTRIBUTION_KEY);
        final String cause_of_death_distribution_file_name = PopulationProperties.getProperties().getProperty(CAUSE_OF_DEATH_DISTRIBUTION_KEY);

        Random random = RandomFactory.getRandom();

        male_first_name_distribution = new MaleFirstNameDistribution(random);
        female_first_name_distribution = new FemaleFirstNameDistribution(random);
        surname_distribution = new SurnameDistribution(random);
        occupation_distribution = new FileBasedEnumeratedDistribution(occupation_distribution_file_name, random);
        cause_of_death_distribution = new FileBasedEnumeratedDistribution(cause_of_death_distribution_file_name, random);

        initialiseProgressIndicator();
    }

    public PopulationToDB(final CompactPopulation population) throws InconsistentWeightException, SQLException, IOException {

        this(population, null);
    }

    private void initialiseProgressIndicator() {

        if (progress_indicator != null) progress_indicator.setTotalSteps(population.size() * 2);
    }

    private void progressStep() {

        if (progress_indicator != null) progress_indicator.progressStep();
    }

    public void export() throws SQLException {

        // TODO read highest existing id from db so don't have to save in file system

        outputIndividuals();
        outputFamilies();

        IDFactory.savePersistentId();
    }

    public void close() throws SQLException {

        connection.close();
    }

    protected void outputIndividuals() throws SQLException {

        for (final CompactPerson compact_person : population) {

            // Ignore if already processed.
            if (!compact_person.isMarked()) {

                // Generate surname for everyone in this tree.
                final String surname = surname_distribution.getSample();
                outputTreeWithSameSurname(compact_person, surname);
            }

            progressStep();
        }
    }

    private void outputTreeWithSameSurname(CompactPerson compact_person, String surname) throws SQLException {

        final List<CompactPerson> descendants = new ArrayList<>();

        descendants.add(compact_person);
        outputDescendants(descendants, surname);
    }

    private void outputDescendants(List<CompactPerson> descendants, String surname) throws SQLException {

        while (!descendants.isEmpty()) {

            final CompactPerson compact_person = descendants.remove(0);
            outputIndividual(compact_person, surname);

            if (compact_person.isMale()) addChildrenToDescendants(compact_person, descendants);
        }
    }

    private void addChildrenToDescendants(CompactPerson compact_person, List<CompactPerson> descendants) {

        if (compact_person.getPartnerships() != null) {

            for (final CompactPartnership partnership : compact_person.getPartnerships()) {

                for (final int child : partnership.getChildren()) {
                    descendants.add(population.getPerson(child));
                }
            }
        }
    }

    private void outputIndividual(CompactPerson compact_person, String surname) throws SQLException {

        compact_person.setMarked(true);

        // TODO factor out
        final PreparedStatement statement = connection.prepareStatement("INSERT INTO " + PopulationProperties.PERSON_TABLE_NAME + " VALUES(?,?,?,?,?,?,?,?,?);");

        statement.setInt(1, compact_person.getId());
        statement.setString(2, compact_person.getSex());
        statement.setString(3, compact_person.isMale() ? male_first_name_distribution.getSample() : female_first_name_distribution.getSample());
        statement.setString(4, surname);
        statement.setDate(5, DateManipulation.daysToSQLDate(compact_person.getDateOfBirth()));
        statement.setDate(6, compact_person.getDateOfDeath() != -1 ? DateManipulation.daysToSQLDate(compact_person.getDateOfDeath()) : null);
        statement.setString(7, occupation_distribution.getSample());
        statement.setString(8, compact_person.getDateOfDeath() != -1 ? cause_of_death_distribution.getSample() : null);
        statement.setString(9, "address");

        statement.executeUpdate();
    }

    protected void outputFamilies() throws SQLException {

        for (final CompactPerson compact_person : population) {

            if (compact_person.getPartnerships() != null) {
                for (final CompactPartnership partnership : compact_person.getPartnerships()) {

                    // Ignore if already processed.
                    if (!partnership.isMarked()) {

                        outputPartnership(partnership);
                    }
                }
            }

            progress_indicator.progressStep();
        }
    }

    protected void outputPartnership(CompactPartnership partnership) throws SQLException {

        partnership.setMarked();

        int partnership_id = partnership.getId();
        java.sql.Date marriage_date = DateManipulation.daysToSQLDate(partnership.getMarriageDate());
        int partner1_id = population.getPerson(partnership.getPartner1()).getId();
        int partner2_id = population.getPerson(partnership.getPartner2()).getId();

        List<Integer> child_ids = getChildIds(partnership);

        importFamily(partnership_id, marriage_date, partner1_id, partner2_id, child_ids);
    }

    private List<Integer> getChildIds(CompactPartnership partnership) {

        List<Integer> child_ids = new ArrayList<>();

        if (partnership.getChildren() != null) {
            for (final int child : partnership.getChildren()) {
                child_ids.add(population.getPerson(child).getId());
            }
        }
        return child_ids;
    }

    private void importFamily(final int familyID, final java.sql.Date marriageDate, final int husbandID, final int wifeID, final List<Integer> childIDs) throws SQLException {

        insertPartnership(familyID, marriageDate);
        insertPartner(familyID, husbandID);
        insertPartner(familyID, wifeID);

        for (final Integer childID : childIDs) {
            insertChild(familyID, childID);
        }
    }

    private void insertPartnership(final int familyID, final java.sql.Date marriageDate) throws SQLException {

        // TODO factor out prepared statement
        final PreparedStatement statement = connection.prepareStatement("INSERT INTO " + PopulationProperties.PARTNERSHIP_TABLE_NAME + " VALUES( ?,? );");
        statement.setInt(1, familyID);
        statement.setDate(2, marriageDate);
        statement.executeUpdate();
    }

    private void insertPartner(final int familyID, final int partnerID) throws SQLException {

        // TODO factor out prepared statement
        final PreparedStatement statement = connection.prepareStatement("INSERT INTO " + PopulationProperties.PARTNERSHIP_PARTNER_TABLE_NAME + " VALUES( ?,? );");
        statement.setInt(1, partnerID);
        statement.setInt(2, familyID);
        statement.executeUpdate();
    }

    private void insertChild(final int familyID, final Integer childID) throws SQLException {

        // TODO factor out prepared statement
        final PreparedStatement statement = connection.prepareStatement("INSERT INTO " + PopulationProperties.PARTNERSHIP_CHILD_TABLE_NAME + " VALUES( ?,? );");
        statement.setInt(1, childID);
        statement.setInt(2, familyID);
        statement.executeUpdate();
    }
}
