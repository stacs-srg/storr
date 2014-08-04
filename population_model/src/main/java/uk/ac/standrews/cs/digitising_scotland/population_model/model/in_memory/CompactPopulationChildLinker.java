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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.NegativeWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.WeightedIntegerDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationLogic;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.util.ProgressIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by graham on 24/07/2014.
 */
public class CompactPopulationChildLinker {

    private static final int MAX_CHILDREN = 6;
    private static final int[] NUMBER_OF_CHILDREN_DISTRIBUTION = new int[]{2, 3, 2, 1, 1, 1, 1};

    private final CompactPerson[] people;
    private final ProgressIndicator progress_indicator;

    private final WeightedIntegerDistribution number_of_children_distribution;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "too expensive...")
    public CompactPopulationChildLinker(final CompactPerson[] people, final ProgressIndicator progress_indicator) throws NegativeWeightException {

        this.people = people;
        this.progress_indicator = progress_indicator;

        number_of_children_distribution = new WeightedIntegerDistribution(0, MAX_CHILDREN, NUMBER_OF_CHILDREN_DISTRIBUTION, RandomFactory.getRandom());
    }

    public void linkChildren() {

        for (final CompactPerson person : people) {

            linkChildren(person);
            progressStep();
        }
    }

    private void linkChildren(final CompactPerson person) {

        final List<CompactPartnership> partnerships = person.getPartnerships();
        if (partnerships != null) {

            for (int i = 0; i < partnerships.size(); i++) {
                final int start_of_next_partnership = i < partnerships.size() - 1 ? partnerships.get(i + 1).getMarriageDate() : Integer.MAX_VALUE;
                linkChildren(partnerships.get(i), start_of_next_partnership);
            }
        }
    }

    private void linkChildren(final CompactPartnership partnership, final int start_of_next_partnership) {

        // Ignore if child_ids have already been linked for this partnership.
        if (partnership.getChildren() == null) {
            final int number_of_children = number_of_children_distribution.getSample();
            linkChildren(partnership, number_of_children, start_of_next_partnership);
        }
    }

    @SuppressWarnings("FeatureEnvy")
    private void linkChildren(final CompactPartnership partnership, final int number_of_children, final int latest_acceptable_birth_date) {

        final List<Integer> children = new ArrayList<>(number_of_children);
        partnership.setChildren(children);

        int previous_child_birth_date = -1;

        int start_index = Math.max(partnership.getPartner1(), partnership.getPartner2()) + 1; // the index at which we start to search for possible child_ids.

        for (int i = 0; i < number_of_children; i++) {

            final int earliest_birth_date = PopulationLogic.earliestAcceptableBirthDate(partnership.getMarriageDate(), previous_child_birth_date);

            final int child_index = findSuitableChild(partnership, start_index, earliest_birth_date, latest_acceptable_birth_date);

            // May not be able to find suitable child if time has run out due to a subsequent marriage, or the whole simulation ending.
            if (child_index == -1) {
                break;
            }

            final CompactPerson child = people[child_index];

            children.add(child_index);
            previous_child_birth_date = child.birth_date;
            child.setHasParents();
            start_index = child_index + 1; // start looking beyond the last child already chosen.
        }
    }

    @SuppressWarnings("FeatureEnvy")
    private int findSuitableChild(final CompactPartnership partnership, final int start_search_index, final int earliest_birth_date, final int latest_birth_date) {

        final int husband_index = getHusbandIndex(partnership);
        final int wife_index = getWifeIndex(partnership);

        final SearchCondition child_compatible = new SearchCondition() {

            @Override
            public ConditionResult check(final int child_index) {

                final CompactPerson child = people[child_index];

                if (child.birth_date >= latest_birth_date) {
                    return ConditionResult.NEGATIVE_STOP;
                }

                final CompactPerson father = people[husband_index];
                final CompactPerson mother = people[wife_index];

                final boolean not_incomer = !child.isIncomer();
                final boolean not_born_too_early = child.birth_date > earliest_birth_date;
                final boolean not_born_too_late = child.birth_date < latest_birth_date;
                final boolean does_not_have_parents = child.hasNoParents();
                final boolean parents_have_sensible_ages = PopulationLogic.parentsHaveSensibleAgesAtChildBirth(father.birth_date, father.death_date, mother.birth_date, mother.death_date, child.birth_date);
                final boolean child_not_married_to_existing_child = !marriedToAnyChildrenOf(child_index, partnership);

                return not_incomer &&
                        not_born_too_early &&
                        not_born_too_late &&
                        does_not_have_parents &&
                        parents_have_sensible_ages &&
                        child_not_married_to_existing_child ?

                        ConditionResult.POSITIVE : ConditionResult.NEGATIVE_CONTINUE;
            }

            private boolean marriedToAnyChildrenOf(final int person_index, final CompactPartnership partnership) {

                // Iterate over all partnerships of the people in the given partnership.
                for (final int partner : new Integer[]{partnership.getPartner1(), partnership.getPartner2()}) {
                    for (final CompactPartnership partnership2 : people[partner].getPartnerships()) {

                        final List<Integer> children = partnership2.getChildren();
                        if (children != null) {
                            for (final int child : children) {
                                if (married(child, person_index)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                return false;
            }

            private boolean married(final int p1_index, final int p2_index) {

                final List<CompactPartnership> partnerships = people[p1_index].getPartnerships();
                if (partnerships != null) {
                    for (final CompactPartnership partnership : partnerships) {
                        if (partnership.getPartner(p1_index) == p2_index) {
                            return true;
                        }
                    }
                }
                return false;
            }
        };

        return findPerson(start_search_index, child_compatible);
    }

    private int findPerson(final int start_index, final SearchCondition condition) {

        final int population_size = people.length;
        for (int i = start_index + 1; i < population_size; i++) {

            switch (condition.check(i)) {

                case POSITIVE:
                    return i;
                case NEGATIVE_CONTINUE:
                    continue;
                case NEGATIVE_STOP:
                    return -1;
            }
        }
        return -1;
    }

    private int getHusbandIndex(final CompactPartnership partnership) {

        final int partner1_index = partnership.getPartner1();
        return people[partner1_index].isMale() ? partner1_index : partnership.getPartner2();
    }

    private int getWifeIndex(final CompactPartnership partnership) {

        final int partner1_index = partnership.getPartner1();
        return !people[partner1_index].isMale() ? partner1_index : partnership.getPartner2();
    }

    private void progressStep() {

        if (progress_indicator != null) {
            progress_indicator.progressStep();
        }
    }
}
