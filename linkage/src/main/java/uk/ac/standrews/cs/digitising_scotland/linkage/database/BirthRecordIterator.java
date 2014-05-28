package uk.ac.standrews.cs.digitising_scotland.linkage.database;

import uk.ac.standrews.cs.digitising_scotland.linkage.event_records.BirthRecord;
import uk.ac.standrews.cs.digitising_scotland.population_model.database.PersonIterator;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.sql.SQLException;

/**
 * Created by graham on 28/05/2014.
 */
public class BirthRecordIterator extends RecordIterator<BirthRecord> {

    private PersonIterator person_iterator;

    public BirthRecordIterator() {

        try {
            person_iterator = new PersonIterator();
            size = person_iterator.size();

        } catch (SQLException e) {
            ErrorHandling.exceptionError(e, "Could not initialise iterator over people.");
        }
    }

    @Override
    public boolean hasNext() {

        return person_iterator.hasNext();
    }

    @Override
    public BirthRecord next() {

        return new BirthRecord(person_iterator.next());
    }

    public void close() {

        try {
            person_iterator.close();

        } catch (SQLException e) {
            ErrorHandling.exceptionError(e, "Could not close iterator over people.");
        }
    }
}
