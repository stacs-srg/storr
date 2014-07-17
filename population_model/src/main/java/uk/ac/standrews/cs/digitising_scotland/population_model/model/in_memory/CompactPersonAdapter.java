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

import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.FemaleFirstNameDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.FileBasedEnumeratedDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.InconsistentWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.MaleFirstNameDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.SurnameDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.AbstractPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.util.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by graham on 01/07/2014.
 */
public class CompactPersonAdapter {

    public static final String OCCUPATION_DISTRIBUTION_KEY = "occupation_distribution_filename";
    public static final String CAUSE_OF_DEATH_DISTRIBUTION_KEY = "cause_of_death_distribution_filename";
    public static final String ADDRESS_DISTRIBUTION_KEY = "address_distribution_filename";

    private final MaleFirstNameDistribution male_first_name_distribution;
    private final FemaleFirstNameDistribution female_first_name_distribution;
    private final SurnameDistribution surname_distribution;
    private final FileBasedEnumeratedDistribution occupation_distribution;
    private final FileBasedEnumeratedDistribution cause_of_death_distribution;
    private final FileBasedEnumeratedDistribution address_distribution;

    private String current_surname;

    public CompactPersonAdapter() throws IOException, InconsistentWeightException {

        final String occupation_distribution_file_name = PopulationProperties.getProperties().getProperty(OCCUPATION_DISTRIBUTION_KEY);
        final String cause_of_death_distribution_file_name = PopulationProperties.getProperties().getProperty(CAUSE_OF_DEATH_DISTRIBUTION_KEY);
        final String address_distribution_file_name = PopulationProperties.getProperties().getProperty(ADDRESS_DISTRIBUTION_KEY);

        Random random = RandomFactory.getRandom();

        male_first_name_distribution = new MaleFirstNameDistribution(random);
        female_first_name_distribution = new FemaleFirstNameDistribution(random);
        surname_distribution = new SurnameDistribution(random);
        occupation_distribution = new FileBasedEnumeratedDistribution(occupation_distribution_file_name, random);
        cause_of_death_distribution = new FileBasedEnumeratedDistribution(cause_of_death_distribution_file_name, random);
        address_distribution = new FileBasedEnumeratedDistribution(address_distribution_file_name, random);

        generateNextSurname();
    }

    public IPerson convertToFullPerson(final CompactPerson person, final int parents_partnership_id) {

        return person != null ? new FullPerson(person, current_surname, parents_partnership_id) : null;
    }

    public void generateNextSurname() {

        current_surname = surname_distribution.getSample();
    }

    private class FullPerson extends AbstractPerson {

        public FullPerson(final CompactPerson person, final String surname, final int parents_partnership_id) {

            id = person.getId();
            sex = person.getSex();

            first_name = person.isMale() ? male_first_name_distribution.getSample() : female_first_name_distribution.getSample();
            this.surname = surname;

            date_of_birth = DateManipulation.daysToDate(person.getBirthDate());
            date_of_death = person.getDeathDate() != -1 ? DateManipulation.daysToDate(person.getDeathDate()) : null;

            occupation = occupation_distribution.getSample();
            cause_of_death = person.getDeathDate() != -1 ? cause_of_death_distribution.getSample() : null;
            address = address_distribution.getSample();
            partnerships = getPartnershipIds(person);

            this.parents_partnership_id = parents_partnership_id;

            string_rep = person.toString();
        }

        private List<Integer> getPartnershipIds(final CompactPerson person) {

            List<CompactPartnership> original_partnerships = person.getPartnerships();
            if (original_partnerships != null) {
                List<Integer> result = new ArrayList<>();
                for (CompactPartnership partnership : original_partnerships) {
                    result.add(partnership.getId());
                }
                return result;
            }
            return null;
        }
    }
}
