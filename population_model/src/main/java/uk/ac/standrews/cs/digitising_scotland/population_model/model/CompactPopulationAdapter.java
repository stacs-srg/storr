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

/**
 * Created by graham on 10/06/2014.
 */
public class CompactPopulationAdapter implements IPopulation {

    private final CompactPerson[] people;

    public CompactPopulationAdapter(CompactPerson[] people) {

        this.people = people;
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

            class PartnershipIterator implements Iterator<IPartnership> {

                int person_index = 0;
                Iterator<CompactPartnership> partnerships = getPartnerships();

                PartnershipIterator() {
                    advanceToNext();
                }

                @Override
                public boolean hasNext() {

                    return person_index < people.length && partnerships.hasNext();
                }

                @Override
                public IPartnership next() {

                    IPartnership result = partnerships.next();
                    advanceToNext();
                    return result;
                }

                @Override
                public void remove() {

                    throw new UnsupportedOperationException("remove");
                }

                private void advanceToNext() {

                    while (!partnerships.hasNext() && person_index < people.length) {
                        person_index++;
                        partnerships = getPartnerships();
                    }
                }

                private Iterator<CompactPartnership> getPartnerships() {

                    List<CompactPartnership> partnerships = person_index < people.length ? people[person_index].getPartnerships() : null;
                    return (partnerships == null ? new ArrayList<CompactPartnership>() : partnerships).iterator();
                }
            }

            @Override
            public Iterator<IPartnership> iterator() {

                return new PartnershipIterator();
            }
        };
    }
}
