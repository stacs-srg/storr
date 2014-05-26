package uk.ac.standrews.cs.digitising_scotland.population_model.database;

import uk.ac.standrews.cs.digitising_scotland.linkage.events.BirthRecord;
import uk.ac.standrews.cs.digitising_scotland.linkage.events.DeathRecord;
import uk.ac.standrews.cs.digitising_scotland.linkage.events.MarriageRecord;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.DBBackedPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.Person;
import uk.ac.standrews.cs.digitising_scotland.util.Condition;
import uk.ac.standrews.cs.digitising_scotland.util.FilteredIterator;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.sql.SQLException;
import java.util.Iterator;

public class EventIterator {

    public static Iterable<BirthRecord> getBirthRecords()  {

        return new Iterable<BirthRecord>() {
            @Override
            public Iterator<BirthRecord> iterator() {
                return new BirthsIterator();
            }
        };
    }

    public static Iterable<DeathRecord> getDeathRecords() {

        return new Iterable<DeathRecord>() {
            @Override
            public Iterator<DeathRecord> iterator() {
                return new DeathsIterator();
            }
        };
    }

    public static Iterable<MarriageRecord> getMarriageRecords() {

        return new Iterable<MarriageRecord>() {
            @Override
            public Iterator<MarriageRecord> iterator() {
                return new MarriagesIterator();
            }
        };
    }

    private static class BirthsIterator implements Iterator<BirthRecord> {

        private Iterator<Person> person_iterator = null;

        public BirthsIterator() {

            try {
                person_iterator = new PersonIterator();
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

        @Override
        public void remove() {

            throw new UnsupportedOperationException("remove");
        }
    }

    private static class DeathsIterator implements Iterator<DeathRecord> {

        private Iterator<Person> person_iterator = null;

        private DeathsIterator() {

            try {
                Condition<Person> check_dead = new Condition<Person>() {
                    @Override
                    public boolean test(Person person) {
                        return person.getDeathDate() != null;
                    }
                };
                person_iterator = new FilteredIterator<>(new PersonIterator(), check_dead);
            } catch (SQLException e) {
                ErrorHandling.exceptionError(e, "Could not initialise iterator over people.");
            }
        }

        @Override
        public boolean hasNext() {

            return person_iterator.hasNext();
        }

        @Override
        public DeathRecord next() {

            return new DeathRecord(person_iterator.next());
        }

        @Override
        public void remove() {

            throw new UnsupportedOperationException("remove");
        }
    }

    private static class MarriagesIterator implements Iterator<MarriageRecord> {

        private Iterator<DBBackedPartnership> partnership_iterator = null;

        private MarriagesIterator() {

            try {
                partnership_iterator = new PartnershipIterator();
            } catch (SQLException e) {
                ErrorHandling.exceptionError(e, "Could not initialise iterator over partnerships.");
            }
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
    }
}
