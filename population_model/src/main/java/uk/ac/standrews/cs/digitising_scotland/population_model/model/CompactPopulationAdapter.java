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
 * Not thread-safe.
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
                    IPartnership result = convertToPartnershipWithIds(next_partnership);
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
    public IPerson findPerson(int id) {
        return population.findPerson(id);
    }

    @Override
    public IPartnership findPartnership(int id) {

        return convertToPartnershipWithIds(population.findPartnership(id));
    }

    @Override
    public int getNumberOfPeople() {
        return population.getNumberOfPeople();
    }

    @Override
    public int getNumberOfPartnerships() {
        return population.getNumberOfPartnerships();
    }

    private Iterator<CompactPartnership> getPartnerships(int person_index) {

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

    private IPartnership convertToPartnershipWithIds(CompactPartnership partnership) {

        return partnership != null ? new PartnershipWithIds(partnership) : null;
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

            children = copyChildren(compact_partnership.getChildren());
        }

        private List<Integer> copyChildren(List<Integer> original_children) {

            List<Integer> children = new ArrayList<>();
            if (original_children != null) {
                for (Integer child_index : original_children) {
                    children.add(populationIndexToId(child_index));
                }
            }
            return children;
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

        @Override
        public boolean equals(Object other) {
            return other instanceof IPartnership && compareTo((IPartnership)other) == 0;
        }
    }
}
