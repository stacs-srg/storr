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
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.NegativeDeviationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.NegativeWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.NormalDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.WeightedIntegerDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationLogic;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;
import uk.ac.standrews.cs.digitising_scotland.util.ProgressIndicator;

import java.util.Random;

/**
 * Created by graham on 24/07/2014.
 */
public class CompactPopulationPartnerLinker {

    /**
     * The approximate average number of days per year.
     */
    public static final float DAYS_PER_YEAR = 365.25f;

    /**
     * The end year of the simulation.
     */
    public static final int END_YEAR = 2013;


    private static final int AGE_AT_FIRST_MARRIAGE_STD_DEV = 3;
    private static final int AGE_AT_FIRST_MARRIAGE_MEAN = 18;
    private static final int MARRIAGE_SEPARATION_MEAN = 7;
    private static final int MARRIAGE_SEPARATION_STD_DEV = 2;
    private static final int MAX_MARRIAGES = 3;

    @SuppressWarnings("MagicNumber")
    private static final int[] NUMBER_OF_MARRIAGES_DISTRIBUTION = new int[]{4, 20, 2, 1};

    private final CompactPerson[] people;
    private final ProgressIndicator progress_indicator;

    private final WeightedIntegerDistribution number_of_marriages_distribution;
    private final NormalDistribution age_at_first_marriage_distribution;
    private final NormalDistribution marriage_separation_distribution;

    private int number_of_partnerships = 0;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "too expensive...")
    public CompactPopulationPartnerLinker(final CompactPerson[] people, final ProgressIndicator progress_indicator) throws NegativeWeightException, NegativeDeviationException {

        this.people = people;
        this.progress_indicator = progress_indicator;

        final Random random = RandomFactory.getRandom();

        number_of_marriages_distribution = new WeightedIntegerDistribution(0, MAX_MARRIAGES, NUMBER_OF_MARRIAGES_DISTRIBUTION, random);
        age_at_first_marriage_distribution = new NormalDistribution(AGE_AT_FIRST_MARRIAGE_MEAN * DAYS_PER_YEAR, AGE_AT_FIRST_MARRIAGE_STD_DEV * DAYS_PER_YEAR, random);
        marriage_separation_distribution = new NormalDistribution(MARRIAGE_SEPARATION_MEAN * DAYS_PER_YEAR, MARRIAGE_SEPARATION_STD_DEV * DAYS_PER_YEAR, random);
    }

    public int linkPartners() {

        for (int i = 0; i < people.length; i++) {

            if (people[i].getPartnerships() == null) { // Skip if marriages have already been set for this person.

                createMarriages(i, number_of_marriages_distribution.getSample());
            }
            progressStep();
        }

        return number_of_partnerships;
    }

    private void createMarriages(final int index, final int number_of_marriages) {

        final CompactPerson person = people[index];

        final int death_date = person.getDeathDate();
        int marriage_date = person.birth_date + (int) (double) age_at_first_marriage_distribution.getSample();

        int start_index = index + 1;

        for (int i = 0; i < number_of_marriages; i++) {

            if (marriage_date < death_date) {
                final int spouse_index = createMarriage(index, start_index, marriage_date);
                if (spouse_index == -1) { // can't find a spouse
                    break;
                }
                start_index = spouse_index + 1;
                marriage_date += marriage_separation_distribution.getSample();
            }
        }
    }

    private int createMarriage(final int first_partner_index, final int start_index, final int marriage_date) {

        if (DateManipulation.daysToYear(marriage_date) <= END_YEAR) {
            final int partner_index = findPartner(first_partner_index, start_index, marriage_date);

            if (partner_index != -1) {
                new CompactPartnership(people[first_partner_index], first_partner_index, people[partner_index], partner_index, marriage_date);
                number_of_partnerships++;
            }

            return partner_index;
        }
        return -1;
    }
    /**
     * Linearly search up people starting at @param index looking for a partner who is sufficiently close in age to the first person and who is either unmarried or not married recently.
     *
     * @return the index if a person is found and -1 otherwise.
     */
    @SuppressWarnings("FeatureEnvy")
    private int findPartner(final int first_partner_index, final int start_index, final int marriage_date) {

        final SearchCondition partner_compatible = new SearchCondition() {

            @Override
            public ConditionResult check(final int person_index) {

                final CompactPerson person = people[first_partner_index];
                final CompactPerson candidate_partner = people[person_index];

                final boolean opposite_sex = person.getSex() != candidate_partner.getSex();
                final boolean age_difference_is_reasonable = PopulationLogic.partnerAgeDifferenceIsReasonable(person.birth_date, candidate_partner.birth_date);
                final boolean not_previously_partners = notPreviouslyPartners(person, first_partner_index, candidate_partner);
                final boolean not_too_many_partnerships = notTooManyPartnerships(candidate_partner);
                final boolean not_too_recent_partnership = notTooRecentPartnership(candidate_partner, marriage_date);
                final boolean still_alive = marriage_date < candidate_partner.getDeathDate();

                return opposite_sex &&
                        age_difference_is_reasonable &&
                        not_previously_partners &&
                        not_too_many_partnerships &&
                        not_too_recent_partnership &&
                        still_alive ? ConditionResult.POSITIVE : ConditionResult.NEGATIVE_CONTINUE;
            }

            private boolean notPreviouslyPartners(final CompactPerson person, final int index, final CompactPerson candidate_partner) {

                if (person.getPartnerships() == null || candidate_partner.getPartnerships() == null) {
                    return true;
                }

                for (final CompactPartnership partnership : person.getPartnerships()) {
                    if (people[partnership.getPartner(index)] == candidate_partner) {
                        return false;
                    }
                }

                return true;
            }

            private boolean notTooManyPartnerships(final CompactPerson candidate_partner) {

                return candidate_partner.getPartnerships() == null || candidate_partner.getPartnerships().size() < MAX_MARRIAGES;
            }

            private boolean notTooRecentPartnership(final CompactPerson candidate_partner, final int marriage_date) {

                final CompactPartnership recent_partnership = candidate_partner.mostRecentPartnership();
                if (recent_partnership == null) {
                    return true;
                }

                final int most_recent_marriage_date = recent_partnership.getMarriageDate();
                return PopulationLogic.longEnoughBetweenMarriages(marriage_date, most_recent_marriage_date);
            }
        };

        return findPerson(start_index, partner_compatible);
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

    private void progressStep() {

        if (progress_indicator != null) {
            progress_indicator.progressStep();
        }
    }
}
