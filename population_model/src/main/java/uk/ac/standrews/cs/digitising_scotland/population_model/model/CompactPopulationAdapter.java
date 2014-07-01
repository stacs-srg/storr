/**
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.population_model.model;

import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.InconsistentWeightException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by graham on 10/06/2014.
 * Not thread-safe.
 */
public class CompactPopulationAdapter implements IPopulation {

    private final CompactPopulation population;
    private final CompactPerson[] people;
    private final CompactPersonAdapter compact_person_adapter;
    private final CompactPartnershipAdapter compact_partnership_adapter;

    public CompactPopulationAdapter(final CompactPopulation population) throws IOException, InconsistentWeightException {

        this.population = population;
        people = population.getPeopleArray();

        compact_person_adapter = new CompactPersonAdapter();
        compact_partnership_adapter = new CompactPartnershipAdapter();
    }

    @Override
    public Iterable<IPerson> getPeople() {

        return new Iterable<IPerson>() {

            @Override
            public Iterator<IPerson> iterator() {

                return new PersonIterator();
            }

            class PersonIterator implements Iterator<IPerson> {

                int person_index = 0;
                CompactPerson next_person = null;

                PersonIterator() {
                    advanceToNext();
                }

                @Override
                public boolean hasNext() {

                    return next_person != null;
                }

                @Override
                public IPerson next() {

                    if (!hasNext()) throw new NoSuchElementException();
                    IPerson result = compact_person_adapter.convertToFullPerson(next_person);
                    advanceToNext();
                    return result;
                }

                @Override
                public void remove() {

                    throw new UnsupportedOperationException("remove");
                }

                private void advanceToNext() {

                    next_person = person_index == people.length ? null : people[person_index++];
                }
            }
        };
    }

    @Override
    public Iterable<IPartnership> getPartnerships() {

        return new Iterable<IPartnership>() {

            @Override
            public Iterator<IPartnership> iterator() {

                unmarkAllPartnerships();
                return new PartnershipIterator();
            }

            class PartnershipIterator implements Iterator<IPartnership> {

                int person_index = 0;
                Iterator<CompactPartnership> partnerships = null;
                CompactPartnership next_partnership = null;

                PartnershipIterator() {
                    advanceToNext();
                }

                @Override
                public boolean hasNext() {

                    return next_partnership != null;
                }

                @Override
                public IPartnership next() {

                    if (!hasNext()) throw new NoSuchElementException();
                    IPartnership result = compact_partnership_adapter.convertToFullPartnership(next_partnership, population);
                    advanceToNext();
                    return result;
                }

                @Override
                public void remove() {

                    throw new UnsupportedOperationException("remove");
                }

                private void advanceToNext() {

                    readFirstPartnerships();
                    readNextUnmarkedPartnership();
                    markPartnership();
                }

                private void readFirstPartnerships() {

                    if (partnerships == null) {
                        partnerships = getPartnerships(0);
                    }
                }

                private void readNextUnmarkedPartnership() {

                    do {
                        readPartnershipsForNextPerson();
                        readNextPartnershipForThisPerson();
                    }
                    while (nextPartnershipIsMarked());
                }

                private boolean nextPartnershipIsMarked() {

                    return next_partnership != null && next_partnership.isMarked();
                }

                private void markPartnership() {

                    if (next_partnership != null) {
                        next_partnership.setMarked(true);
                    }
                }

                private void readPartnershipsForNextPerson() {

                    while (person_index < people.length && !partnerships.hasNext()) {
                        partnerships = getPartnerships(++person_index);
                    }
                }

                private void readNextPartnershipForThisPerson() {

                    next_partnership = partnerships.hasNext() ? partnerships.next() : null;
                }
            }
        };
    }

    @Override
    public IPerson findPerson(final int id) {

        return compact_person_adapter.convertToFullPerson(population.findPerson(id));
    }

    @Override
    public IPartnership findPartnership(final int id) {

        return compact_partnership_adapter.convertToFullPartnership(population.findPartnership(id), population);
    }

    @Override
    public int getNumberOfPeople() {
        return population.getNumberOfPeople();
    }

    @Override
    public int getNumberOfPartnerships() {
        return population.getNumberOfPartnerships();
    }

    private Iterator<CompactPartnership> getPartnerships(final int person_index) {

        List<CompactPartnership> partnerships = person_index < people.length ? people[person_index].getPartnerships() : null;
        return (partnerships == null ? new ArrayList<CompactPartnership>() : partnerships).iterator();
    }

    private void unmarkAllPartnerships() {

        for (CompactPerson person : people) {
            List<CompactPartnership> partnerships = person.getPartnerships();
            if (partnerships != null) {
                for (CompactPartnership partnership : partnerships) {
                    partnership.setMarked(false);
                }
            }
        }
    }
}
