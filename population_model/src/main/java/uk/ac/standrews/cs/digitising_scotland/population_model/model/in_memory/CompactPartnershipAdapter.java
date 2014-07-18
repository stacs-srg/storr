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
package uk.ac.standrews.cs.digitising_scotland.population_model.model.in_memory;

import uk.ac.standrews.cs.digitising_scotland.population_model.model.AbstractPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by graham on 01/07/2014.
 */
public class CompactPartnershipAdapter {

    public static IPartnership convertToFullPartnership(final CompactPartnership partnership, final CompactPopulation population) {

        return partnership != null ? new FullPartnership(partnership, population) : null;
    }

    private static class FullPartnership extends AbstractPartnership {

        private final CompactPopulation population;

        @SuppressWarnings("FeatureEnvy")
        FullPartnership(final CompactPartnership compact_partnership, final CompactPopulation population) {

            this.population = population;

            id = compact_partnership.getId();

            final CompactPerson partner1 = population.getPerson(compact_partnership.getPartner1());
            final CompactPerson partner2 = population.getPerson(compact_partnership.getPartner2());

            final boolean partner1_is_male = partner1.isMale();

            male_partner_id = partner1_is_male ? partner1.getId() : partner2.getId();
            female_partner_id = partner1_is_male ? partner2.getId() : partner1.getId();

            marriage_date = DateManipulation.daysToDate(compact_partnership.getMarriageDate());

            children = copyChildren(compact_partnership.getChildren());
        }

        private int populationIndexToId(final int index) {
            return population.getPerson(index).getId();
        }

        private List<Integer> copyChildren(final List<Integer> original_children) {

            final List<Integer> children = new ArrayList<>();
            if (original_children != null) {
                for (final Integer child_index : original_children) {
                    children.add(populationIndexToId(child_index));
                }
            }
            return children;
        }
    }
}
