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
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeDeviationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeWeightException;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by graham on 02/07/2014.
 */
public abstract class PopulationStructureTest {

    protected IPopulation makePopulation(int population_size) throws IOException, InconsistentWeightException {

        return new CompactPopulationAdapter(new CompactPopulation(makePeople(population_size), 0, 0));
    }

    protected static CompactPerson[] makePeople(int n) {

        CompactPerson[] result = new CompactPerson[n];
        for (int i = 0; i < n; i++) {
            result[i] = new CompactPerson(0, true);
            result[i].id = i + 1;
        }
        return result;
    }

    protected static List<Object[]> expandWithBooleanOptions(Object[]... options) {

        List<Object[]> result = new ArrayList<>();

        for (Object[] test_config : options) {

            Object[] expanded_config1 = new Object[test_config.length + 1];
            for (int i = 0; i < test_config.length; i++) {
                expanded_config1[i] = test_config[i];
            }

            Object[] expanded_config2 = expanded_config1.clone();

            expanded_config1[expanded_config1.length - 1] = false;
            expanded_config2[expanded_config2.length - 1] = true;

            result.add(expanded_config1);
            result.add(expanded_config2);
        }
        return result;
    }

    protected static Object[] unconnectedPopulation(int size) throws IOException, InconsistentWeightException {

        CompactPerson[] people = makePeople(size);
        IPopulation population = new CompactPopulationAdapter(new CompactPopulation(people, 0, 0));
        population.setDescription("unconnected-population-" + size);

        int[] expected_people_id_order = getIds(people);
        int[] expected_partnership_id_order = new int[]{};

        return new Object[]{population, expected_people_id_order, expected_partnership_id_order};
    }

    protected static Object[] fullPopulation(int size) throws IOException, InconsistentWeightException, NegativeDeviationException, NegativeWeightException {

        CompactPopulation compact_population = new CompactPopulation(size);
        IPopulation population = new CompactPopulationAdapter(compact_population);
        population.setDescription("full-population-" + size);

        return new Object[]{population, null, null};  // Don't know what order to expect them in.
    }

    protected static Object[] populationWithOnePartnership() throws IOException, InconsistentWeightException {

        CompactPerson[] people = makePeople(3);
        CompactPartnership partnership = createPartnership(people, 0, 1);

        IPopulation population = new CompactPopulationAdapter(new CompactPopulation(people, 0, 0));
        population.setDescription("population-1-partnership");

        int[] expected_people_id_order = getIds(people);
        int[] expected_partnership_id_order = new int[]{partnership.getId()};

        return new Object[]{population, expected_people_id_order, expected_partnership_id_order};
    }

    protected static Object[] populationWithThreePartnerships() throws IOException, InconsistentWeightException {

        CompactPerson[] people = makePeople(6);

        CompactPartnership partnership1 = createPartnership(people, 0, 1);
        CompactPartnership partnership2 = createPartnership(people, 2, 3);
        CompactPartnership partnership3 = createPartnership(people, 4, 5);

        IPopulation population = new CompactPopulationAdapter(new CompactPopulation(people, 0, 0));
        population.setDescription("population-3-partnerships");

        int[] expected_people_id_order = getIds(people);
        int[] expected_partnership_id_order = new int[]{partnership1.getId(), partnership2.getId(), partnership3.getId()};

        return new Object[]{population, expected_people_id_order, expected_partnership_id_order};
    }

    protected static Object[] populationWithTwoFamilies() throws IOException, InconsistentWeightException, ParseException {

        CompactPerson[] people = makePeopleInTwoFamilies();
        CompactPartnership partnership1 = people[0].getPartnerships().get(0);
        CompactPartnership partnership2 = people[2].getPartnerships().get(0);
        CompactPartnership[] partnerships = new CompactPartnership[]{partnership1, partnership2};

        IPopulation population = new CompactPopulationAdapter(new CompactPopulation(people, 0, 0));
        population.setDescription("population-2-families");

        int[] expected_people_id_order = new int[]{1, 5, 6, 2, 3, 4, 7, 8};

        int[] expected_partnership_id_order = getIds(partnerships);

        return new Object[]{population, expected_people_id_order, expected_partnership_id_order};
    }

    private static int[] getIds(CompactPerson[] people) {

        int[] ids = new int[people.length];
        for (int i = 0; i < people.length; i++) {
            ids[i] = people[i].getId();
        }
        return ids;
    }

    private static int[] getIds(CompactPartnership[] partnerships) {

        int[] ids = new int[partnerships.length];
        for (int i = 0; i < partnerships.length; i++) {
            ids[i] = partnerships[i].getId();
        }
        return ids;
    }

    private static CompactPartnership createPartnership(CompactPerson[] people, int partner1_index, int partner2_index) {

        people[partner1_index].setMale(false);
        people[partner2_index].setMale(true);

        return new CompactPartnership(people[partner1_index], partner1_index, people[partner2_index], partner2_index, 0);
    }

    private static CompactPerson[] makePeopleInTwoFamilies() throws IOException, InconsistentWeightException, ParseException {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        int fatherA_birth_date = DateManipulation.dateToDays(DateManipulation.parseDate("21/08/1888", formatter));
        int motherA_birth_date = DateManipulation.dateToDays(DateManipulation.parseDate("01/01/1890", formatter));
        int child1A_birth_date = DateManipulation.dateToDays(DateManipulation.parseDate("17/11/1910", formatter));
        int child2A_birth_date = DateManipulation.dateToDays(DateManipulation.parseDate("31/12/1913", formatter));

        int fatherB_birth_date = DateManipulation.dateToDays(DateManipulation.parseDate("21/07/1860", formatter));
        int motherB_birth_date = DateManipulation.dateToDays(DateManipulation.parseDate("23/02/1891", formatter));
        int child1B_birth_date = DateManipulation.dateToDays(DateManipulation.parseDate("21/08/1920", formatter));
        int child2B_birth_date = DateManipulation.dateToDays(DateManipulation.parseDate("21/08/1925", formatter));

        CompactPerson fatherA = new CompactPerson(fatherA_birth_date, true, 1);
        CompactPerson motherA = new CompactPerson(motherA_birth_date, false, 2);
        CompactPerson motherB = new CompactPerson(motherB_birth_date, false, 3);
        CompactPerson fatherB = new CompactPerson(fatherB_birth_date, true, 4);
        CompactPerson child1A = new CompactPerson(child1A_birth_date, false, 5);
        CompactPerson child1B = new CompactPerson(child1B_birth_date, true, 6);
        CompactPerson child2A = new CompactPerson(child2A_birth_date, true, 7);
        CompactPerson child2B = new CompactPerson(child2B_birth_date, false, 8);

        CompactPerson[] population = new CompactPerson[]{fatherA, motherA, motherB, fatherB, child1A, child1B, child2A, child2B};

        CompactPartnership partnershipA = new CompactPartnership(0, 1, 0);
        CompactPartnership partnershipB = new CompactPartnership(2, 3, 0);

        List<CompactPartnership> partnershipsFatherA = new ArrayList<>();
        List<CompactPartnership> partnershipsMotherA = new ArrayList<>();
        List<CompactPartnership> partnershipsFatherB = new ArrayList<>();
        List<CompactPartnership> partnershipsMotherB = new ArrayList<>();

        partnershipsFatherA.add(partnershipA);
        partnershipsMotherA.add(partnershipA);
        partnershipsFatherB.add(partnershipB);
        partnershipsMotherB.add(partnershipB);

        fatherA.setPartnerships(partnershipsFatherA);
        motherA.setPartnerships(partnershipsMotherA);
        fatherB.setPartnerships(partnershipsFatherB);
        motherB.setPartnerships(partnershipsMotherB);

        List<Integer> childrenA = new ArrayList<>();
        List<Integer> childrenB = new ArrayList<>();

        childrenA.add(4);
        childrenA.add(6);
        childrenB.add(5);
        childrenB.add(7);

        partnershipA.setChildren(childrenA);
        partnershipB.setChildren(childrenB);

        return population;
    }
}
