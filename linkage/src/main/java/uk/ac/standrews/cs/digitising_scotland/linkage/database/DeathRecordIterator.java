package uk.ac.standrews.cs.digitising_scotland.linkage.database;

import uk.ac.standrews.cs.digitising_scotland.linkage.event_records.DeathRecord;
import uk.ac.standrews.cs.digitising_scotland.population_model.database.PersonIterator;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.Person;
import uk.ac.standrews.cs.digitising_scotland.util.Condition;
import uk.ac.standrews.cs.digitising_scotland.util.FilteredIterator;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.sql.SQLException;
import java.util.Iterator;

/**
 * Created by graham on 28/05/2014.
 */
public class DeathRecordIterator implements Iterator<DeathRecord>, Iterable<DeathRecord> {

    private Iterator<Person> dead_person_iterator;
    private PersonIterator person_iterator;

    // The size is an approximation since it will also include live people that won't be returned by the
    // main iterator.
    private int size;

    public DeathRecordIterator() {

        try {
            Condition<Person> check_dead = new Condition<Person>() {
                @Override
                public boolean test(Person person) {
                    return person.getDeathDate() != null;
                }
            };

            person_iterator = new PersonIterator();
            size = person_iterator.size();

            dead_person_iterator = new FilteredIterator<>(person_iterator, check_dead);

        } catch (SQLException e) {
            ErrorHandling.exceptionError(e, "Could not initialise iterator over people.");
        }
    }

    @Override
    public Iterator<DeathRecord> iterator() {

        return this;
    }

    @Override
    public boolean hasNext() {

        return dead_person_iterator.hasNext();
    }

    @Override
    public DeathRecord next() {

        return new DeathRecord(dead_person_iterator.next());
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
            person_iterator.close();

        } catch (SQLException e) {
            ErrorHandling.exceptionError(e, "Could not close iterator over people.");
        }
    }
}
