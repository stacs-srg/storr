package uk.ac.standrews.cs.digitising_scotland.linkage.old;

import uk.ac.standrews.cs.digitising_scotland.linkage.event_records.BirthRecord;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.database.old.PersonIterator;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.sql.SQLException;

/**
 * Created by graham on 28/05/2014.
 */
public class BirthRecordIterator extends RecordIterator2<BirthRecord> {

    private PersonIterator person_iterator;

    public BirthRecordIterator() {

        try {
            person_iterator = new PersonIterator();

        } catch (Exception e) {
            ErrorHandling.exceptionError(e, "Could not initialise iterator over people.");
        }
    }

    @Override
    public boolean hasNext() {

        return person_iterator.hasNext();
    }

    @Override
    public BirthRecord next() {

        return new BirthRecord(person_iterator.next(), null);
    }

    public void close() {

        try {
            person_iterator.close();

        } catch (SQLException e) {
            ErrorHandling.exceptionError(e, "Could not close iterator over people.");
        }
    }
}
