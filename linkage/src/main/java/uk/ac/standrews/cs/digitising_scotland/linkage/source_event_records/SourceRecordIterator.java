package uk.ac.standrews.cs.digitising_scotland.linkage.source_event_records;

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
public class SourceRecordIterator {

    public static Iterable<BirthSourceSourceSourceRecord> getBirthRecordIterator(final IPopulation population) {

        return new Iterable<BirthSourceSourceSourceRecord>() {

            @Override
            public Iterator<BirthSourceSourceSourceRecord> iterator() {

                Iterator<IPerson> person_iterator = population.getPeople().iterator();

                Map<IPerson, BirthSourceSourceSourceRecord> person_to_birth_record_mapper = new Map<IPerson, BirthSourceSourceSourceRecord>() {
                    @Override
                    public BirthSourceSourceSourceRecord map(IPerson person) {
                        return new BirthSourceSourceSourceRecord(person, population);
                    }
                };

                return new MappedIterator<>(person_iterator, person_to_birth_record_mapper);
            }
        };
    }

    public static Iterable<DeathSourceSourceSourceRecord> getDeathRecordIterator(final IPopulation population) {

        return new Iterable<DeathSourceSourceSourceRecord>() {

            @Override
            public Iterator<DeathSourceSourceSourceRecord> iterator() {

                Condition<IPerson> check_dead = new Condition<IPerson>() {
                    @Override
                    public boolean test(final IPerson person) {
                        return person.getDeathDate() != null;
                    }
                };

                Iterator<IPerson> dead_person_iterator = new FilteredIterator<>(population.getPeople().iterator(), check_dead);

                Map<IPerson, DeathSourceSourceSourceRecord> person_to_death_record_mapper = new Map<IPerson, DeathSourceSourceSourceRecord>() {
                    @Override
                    public DeathSourceSourceSourceRecord map(IPerson person) {
                        return new DeathSourceSourceSourceRecord(person, population);
                    }
                };

                return new MappedIterator<>(dead_person_iterator, person_to_death_record_mapper);
            }
        };
    }

    public static Iterable<MarriageSourceSourceRecord> getMarriageRecordIterator(final IPopulation population) {

        return new Iterable<MarriageSourceSourceRecord>() {

            @Override
            public Iterator<MarriageSourceSourceRecord> iterator() {

                Iterator<IPartnership> partnership_iterator = population.getPartnerships().iterator();

                Map<IPartnership, MarriageSourceSourceRecord> person_to_marriage_record_mapper = new Map<IPartnership, MarriageSourceSourceRecord>() {
                    @Override
                    public MarriageSourceSourceRecord map(IPartnership partnership) {
                        return new MarriageSourceSourceRecord(partnership, population);
                    }
                };

                return new MappedIterator<>(partnership_iterator, person_to_marriage_record_mapper);
            }
        };
    }
}
