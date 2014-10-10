package uk.ac.standrews.cs.digitising_scotland.linkage.tools;

import uk.ac.standrews.cs.digitising_scotland.linkage.source_event_records.SourceRecordGenerator;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.database.DBPopulationAdapter;

/**
 * Generates birth/death/marriage records for all the people in the database.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Alan Dearle (al@st-andrews.ac.uk)
 */
public class GenerateEventRecordsFromDB {

    public static void main(final String[] args) throws Exception {

        IPopulation population = new DBPopulationAdapter();
        SourceRecordGenerator generator = new SourceRecordGenerator(population);
        generator.generateEventRecords(args);
    }
}
