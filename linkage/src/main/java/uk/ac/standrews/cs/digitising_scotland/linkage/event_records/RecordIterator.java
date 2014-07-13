package uk.ac.standrews.cs.digitising_scotland.linkage.event_records;

import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.util.Condition;
import uk.ac.standrews.cs.digitising_scotland.util.FilteredIterator;
import uk.ac.standrews.cs.digitising_scotland.util.Map;
import uk.ac.standrews.cs.digitising_scotland.util.MappedIterator;

import java.util.Iterator;

/**
 * Created by graham on 10/07/2014.
 */
public class RecordIterator {

    public static Iterable<BirthRecord> getBirthRecordIterator(final IPopulation population) {

        return new Iterable<BirthRecord>() {

            @Override
            public Iterator<BirthRecord> iterator() {

                Iterator<IPerson> person_iterator = population.getPeople().iterator();

                Map<IPerson, BirthRecord> person_to_birth_record_mapper = new Map<IPerson, BirthRecord>() {
                    @Override
                    public BirthRecord map(IPerson person) {
                        return new BirthRecord(person, population);
                    }
                };

                return new MappedIterator<>(person_iterator, person_to_birth_record_mapper);
            }
        };
    }

    public static Iterable<DeathRecord> getDeathRecordIterator(final IPopulation population) {

        return new Iterable<DeathRecord>() {

            @Override
            public Iterator<DeathRecord> iterator() {

                Condition<IPerson> check_dead = new Condition<IPerson>() {
                    @Override
                    public boolean test(final IPerson person) {
                        return person.getDeathDate() != null;
                    }
                };

                Iterator<IPerson> dead_person_iterator = new FilteredIterator<>(population.getPeople().iterator(), check_dead);

                Map<IPerson, DeathRecord> person_to_death_record_mapper = new Map<IPerson, DeathRecord>() {
                    @Override
                    public DeathRecord map(IPerson person) {
                        return new DeathRecord(person, population);
                    }
                };

                return new MappedIterator<>(dead_person_iterator, person_to_death_record_mapper);
            }
        };
    }

    public static Iterable<MarriageRecord> getMarriageRecordIterator(final IPopulation population) {

        return new Iterable<MarriageRecord>() {

            @Override
            public Iterator<MarriageRecord> iterator() {

                Iterator<IPartnership> partnership_iterator = population.getPartnerships().iterator();

                Map<IPartnership, MarriageRecord> person_to_marriage_record_mapper = new Map<IPartnership, MarriageRecord>() {
                    @Override
                    public MarriageRecord map(IPartnership partnership) {
                        return new MarriageRecord(partnership, population);
                    }
                };

                return new MappedIterator<>(partnership_iterator, person_to_marriage_record_mapper);
            }
        };
    }
}
