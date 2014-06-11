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

import uk.ac.standrews.cs.digitising_scotland.util.ArrayIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by graham on 10/06/2014.
 */
public class CompactPopulationAdapter implements IPopulation {

    private final CompactPopulation population;
    private final CompactPerson[] people;

    public CompactPopulationAdapter(CompactPopulation population) {

        this.population = population;
        people = population.getPeopleArray();
    }

    @Override
    public Iterable<IPerson> getPeople() {

        return new Iterable<IPerson>() {

            @Override
            public Iterator<IPerson> iterator() {

                return new ArrayIterator<IPerson>(people);
            }
        };
    }

    @Override
    public Iterable<IPartnership> getPartnerships() {

        return new Iterable<IPartnership>() {

            @Override
            public Iterator<IPartnership> iterator() {

                return new PartnershipIterator();
            }

            class PartnershipIterator implements Iterator<IPartnership> {

                int person_index = 0;
                Iterator<CompactPartnership> partnerships = null;
                IPartnership next_partnership = null;

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
                    IPartnership result = next_partnership;
                    advanceToNext();
                    return result;
                }

                @Override
                public void remove() {

                    throw new UnsupportedOperationException("remove");
                }

                private void advanceToNext() {

                    if (partnerships == null) {
                        partnerships = getPartnerships(0);
                    }

                    while (person_index < people.length && !partnerships.hasNext()) {
                        person_index++;
                        partnerships = getPartnerships(person_index);
                    }

                    next_partnership = partnerships.hasNext() ? partnerships.next() : null;
                }
            }
        };
    }

    @Override
    public Iterable<Object> getPopulation() {

        return new Iterable<Object>() {

            @Override
            public Iterator<Object> iterator() {

                return new PopulationIterator();
            }

            class PopulationIterator implements Iterator<Object> {

                int person_index = -1;
                Iterator<CompactPartnership> partnerships = null;
                boolean return_person_next_time = true;
                Object next_object = null;

                PopulationIterator() {
                    advanceToNext();
                }

                @Override
                public boolean hasNext() {

                    return next_object != null;
                }

                @Override
                public Object next() {

                    if (!hasNext()) throw new NoSuchElementException();
                    Object result = next_object;
                    advanceToNext();
                    return result;
                }

                @Override
                public void remove() {

                    throw new UnsupportedOperationException("remove");
                }

                private void advanceToNext() {

                    if (return_person_next_time || !partnerships.hasNext()) {
                        readNextPerson();
                    } else {
                        readNextPartnership();
                    }
                }

                private void readNextPerson() {

                    person_index++;

                    if (person_index < people.length) {

                        next_object = people[person_index];
                        partnerships = getPartnerships(person_index);
                        return_person_next_time = !partnerships.hasNext();

                    } else {
                        next_object = null;
                    }
                }

                private void readNextPartnership() {
                    next_object = partnerships.next();
                }
            }
        };
    }

    @Override
    public IPerson findPerson(int id) {
        return population.findPerson(id);
    }

    @Override
    public IPartnership findPartnership(int id) {
        return population.findPartnership(id);
    }

    @Override
    public int size() {
        return people.length;
    }

    private Iterator<CompactPartnership> getPartnerships(int person_index) {

        List<CompactPartnership> partnerships = person_index < people.length ? people[person_index].getPartnerships() : null;
        return (partnerships == null ? new ArrayList<CompactPartnership>() : partnerships).iterator();
    }
}
