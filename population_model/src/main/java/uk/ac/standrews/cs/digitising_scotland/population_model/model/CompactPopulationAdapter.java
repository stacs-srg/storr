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
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.ArrayList;
import java.util.Date;
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

                    next_partnership = partnerships.hasNext() ? new PartnershipWithIds(partnerships.next()) : null;
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
        CompactPartnership partnership = population.findPartnership(id);
        return partnership != null ? new PartnershipWithIds(partnership) : null;
    }

    @Override
    public int size() {
        return people.length;
    }

    private Iterator<CompactPartnership> getPartnerships(int person_index) {

        List<CompactPartnership> partnerships = person_index < people.length ? people[person_index].getPartnerships() : null;
        return (partnerships == null ? new ArrayList<CompactPartnership>() : partnerships).iterator();
    }

    private class PartnershipWithIds implements IPartnership {

        private int id;
        private int partner1_id;
        private int partner2_id;
        private Date marriage_date;
        private List<Integer> children;

        private int populationIndexToId(int index) {
            return population.getPerson(index).getId();
        }

        PartnershipWithIds(CompactPartnership compact_partnership) {

            id = compact_partnership.getId();
            partner1_id = populationIndexToId(compact_partnership.getPartner1());
            partner2_id = populationIndexToId(compact_partnership.getPartner2());
            marriage_date = DateManipulation.daysToDate(compact_partnership.getMarriageDate());

            children = new ArrayList<>();
            List<Integer> original_children = compact_partnership.getChildren();
            if (original_children != null) {
                for (Integer child_index : original_children) {
                    children.add(populationIndexToId(child_index));
                }
            }
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public int getPartner1Id() {
            return partner1_id;
        }

        @Override
        public int getPartner2Id() {
            return partner2_id;
        }

        @Override
        public Date getMarriageDate() {
            return marriage_date;
        }

        @Override
        public List<Integer> getChildren() {
            return children;
        }

        @Override
        public int compareTo(IPartnership other) {
            return id - other.getId();
        }
    }
}
