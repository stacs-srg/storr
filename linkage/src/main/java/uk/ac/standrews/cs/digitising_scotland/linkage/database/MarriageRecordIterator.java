package uk.ac.standrews.cs.digitising_scotland.linkage.database;

import uk.ac.standrews.cs.digitising_scotland.linkage.event_records.MarriageRecord;
import uk.ac.standrews.cs.digitising_scotland.population_model.database.PartnershipIterator;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.sql.SQLException;
import java.util.Iterator;

/**
 * Created by graham on 28/05/2014.
 */
public class MarriageRecordIterator implements Iterator<MarriageRecord>, Iterable<MarriageRecord> {

    private PartnershipIterator partnership_iterator;
    private int size;

    public MarriageRecordIterator() {

        try {
            partnership_iterator = new PartnershipIterator();
            size = partnership_iterator.size();

        } catch (SQLException e) {
            ErrorHandling.exceptionError(e, "Could not initialise iterator over partnerships.");
        }
    }

    @Override
    public Iterator<MarriageRecord> iterator() {

        return this;
    }

    @Override
    public boolean hasNext() {

        return partnership_iterator.hasNext();
    }

    @Override
    public MarriageRecord next() {

        return new MarriageRecord(partnership_iterator.next());
    }

    @Override
    public void remove() {

        throw new UnsupportedOperationException("remove");
    }

    public int size() {

        return size;
    }

    public void close() {

        try {
            partnership_iterator.close();

        } catch (SQLException e) {
            ErrorHandling.exceptionError(e, "Could not close iterator over partnerships.");
        }
    }
}
