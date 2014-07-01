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
package uk.ac.standrews.cs.digitising_scotland.population_model.transform.old;

import uk.ac.standrews.cs.digitising_scotland.population_model.transform.old.PopulationToFile;
import uk.ac.standrews.cs.digitising_scotland.util.ArrayIterator;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.InconsistentWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.Person;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Writes a representation of the population to file in GEDCOM format.
 * 
 * GEDCOM specification: http://homepages.rootsweb.ancestry.com/~pmcbride/gedcom/55gctoc.htm
 * 
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
public class PopulationToGEDCOM extends PopulationToFile {

    private static final String CHAR_SET = "ASCII";
    private static final String GEDCOM_FORM = "LINEAGE-LINKED";
    private static final String GEDCOM_VERSION = "5.5";
    private static final String SOURCE_SOFTWARE = "Digitising_Scotland";
    private static final String SUBMITTER = "Digitising Scotland Project";

    private final List<Integer> exported_family_ids = new ArrayList<>(); // used to not those families already exported

    /**
     * Initialises the exporter. This includes potentially expensive scanning of the population graph.
     * 
     * @param population the population
     * @param path the path for the output file
     * @throws IOException if the file does not exist and cannot be created
     */
    public PopulationToGEDCOM(final CompactPopulation population, final String path) throws IOException, InconsistentWeightException {

        super(population, path);
    }

    @Override
    protected void outputHeader(final PrintWriter writer) {

        writer.println("0 HEAD");
        writer.println("1 SOUR " + SOURCE_SOFTWARE);
        writer.println("1 SUBM @S1@");
        writer.println("1 GEDC");
        writer.println("2 VERS " + GEDCOM_VERSION);
        writer.println("2 FORM " + GEDCOM_FORM);
        writer.println("1 CHAR " + CHAR_SET);
    }

    @Override
    protected void outputIndividual(final PrintWriter writer, final int index, final CompactPerson compact_person, final Person person) {

        writer.println("0 @" + padId(person.getId()) + "@ INDI");
        writer.println("1 NAME " + person.getFirstName() + " /" + person.getSurname() + "/");
        writer.println("1 SEX " + person.getSex());
        writer.println("1 BIRT");
        writer.println("2 DATE " + DateManipulation.daysToString(compact_person.getDateOfBirth()));

        if (compact_person.getDateOfDeath() > 0) {
            writer.println("1 DEAT");
            writer.println("2 DATE " + DateManipulation.daysToString(compact_person.getDateOfDeath()));
        }

        final List<String> families_where_parent = getIdsOfFamiliesWhereSpouse(compact_person);
        final List<String> families_where_child = getIdsOfFamiliesWhereChild(index);

        for (final String family_id : families_where_parent) {
            writer.println("1 FAMS @" + family_id + "@");
        }

        for (final String family_id : families_where_child) {
            writer.println("1 FAMC @" + family_id + "@");
        }
    }

    @Override
    protected void outputFamilies(final PrintWriter writer) {

        Iterator people = new ArrayIterator(population.getPeopleArray());

        while(people.hasNext()) {
            CompactPerson compact_person = (CompactPerson)people.next();
            if (compact_person.getPartnerships() != null) {
                for (final CompactPartnership partnership : compact_person.getPartnerships()) {

                    final int id = partnership.getId();

                    if (!exported_family_ids.contains(id)) {

                        exported_family_ids.add(id);

                        writer.println("0 @" + padId(id) + "@ FAM");
                        if (partnership.getMarriageDate() > -1) {
                            writer.println("1 MARR");
                            writer.println("2 DATE " + DateManipulation.daysToString(partnership.getMarriageDate()));
                        }
                        int father = partnership.getPartner1();
                        int mother = partnership.getPartner2();
                        if (!population.getPerson(father).isMale()) {
                            // swap if father is not male - assumes different sexes of spouses.
                            final int temp = father;
                            father = mother;
                            mother = temp;
                        }
                        writer.println("1 HUSB @" + padId(population.getPerson(father).getId()) + "@");
                        writer.println("1 WIFE @" + padId(population.getPerson(mother).getId()) + "@");
                        if (partnership.getChildren() != null) {
                            for (final int child : partnership.getChildren()) {
                                writer.println("1 CHIL @" + padId(population.getPerson(child).getId()) + "@");
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void outputTrailer(final PrintWriter writer) {

        writer.println("0 @S1@ SUBM\n" + "1 NAME " + SUBMITTER);
        writer.println("0 TRLR");
    }

    private List<String> getIdsOfFamiliesWhereChild(final int child) {

        final List<String> ids = new ArrayList<String>();
        Iterator people = new ArrayIterator(population.getPeopleArray());

        while(people.hasNext()) {
            CompactPerson person = (CompactPerson)people.next();
            if (person.getPartnerships() != null) {
                for (final CompactPartnership partnership : person.getPartnerships()) {
                    if (partnership.includesChild(child)) {
                        ids.add(padId(partnership.getId()));
                    }
                }
            }
        }

        return ids;
    }

    private List<String> getIdsOfFamiliesWhereSpouse(final CompactPerson spouse) {

        final List<String> ids = new ArrayList<String>();
        if (spouse.getPartnerships() != null) {
            for (final CompactPartnership partnership : spouse.getPartnerships()) {
                ids.add(padId(partnership.getId()));
            }
        }

        return ids;
    }
}
