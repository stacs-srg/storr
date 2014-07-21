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
package uk.ac.standrews.cs.digitising_scotland.population_model.model.gedcom;

import uk.ac.standrews.cs.digitising_scotland.population_model.model.AbstractFilePopulationWriter;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

/**
 * Writes a representation of the population to file in GEDCOM format.
 *
 * GEDCOM specification: http://homepages.rootsweb.ancestry.com/~pmcbride/gedcom/55gctoc.htm
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
public class PopulationToGEDCOM extends AbstractFilePopulationWriter {

    private static final String CHAR_SET = "ASCII";
    private static final String GEDCOM_FORM = "LINEAGE-LINKED";
    private static final String GEDCOM_VERSION = "5.5.1";
    private static final String SOURCE_SOFTWARE = "Digitising_Scotland";
    private static final String SUBMITTER = "Digitising Scotland Project";

    /**
     * Initialises the exporter. This includes potentially expensive scanning of the population graph.
     *
     * @param path_string the path for the output file
     * @throws IOException if the file does not exist and cannot be created
     */
    public PopulationToGEDCOM(final String path_string) throws IOException {

        super(path_string);
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

    @SuppressWarnings("FeatureEnvy")
    @Override
    public void recordPerson(final IPerson person) {

        writer.println("0 @" + individualLabel(person.getId()) + "@ INDI");
        writer.println("1 NAME " + person.getFirstName() + " /" + person.getSurname() + '/');
        writer.println("1 SEX " + person.getSex());
        writer.println("1 BIRT");
        writer.println("2 DATE " + DateManipulation.formatDate(person.getBirthDate()));

        final Date death_date = person.getDeathDate();
        if (death_date != null) {
            writer.println("1 DEAT");
            writer.println("2 DATE " + DateManipulation.formatDate(death_date));
        }

        final List<Integer> partnership_ids = person.getPartnerships();

        if (partnership_ids != null) {
            for (final int partnership_id : partnership_ids) {
                writer.println("1 FAMS @" + familyLabel(partnership_id) + '@');
            }
        }

        final int parents_partnership_id = person.getParentsPartnership();
        if (parents_partnership_id != -1) {
            writer.println("1 FAMC @" + familyLabel(parents_partnership_id) + '@');
        }
    }

    @SuppressWarnings("FeatureEnvy")
    @Override
    public void recordPartnership(final IPartnership partnership) {

        final int partnership_id = partnership.getId();

        writer.println("0 @" + familyLabel(partnership_id) + "@ FAM");
        if (partnership.getMarriageDate() != null) {
            writer.println("1 MARR");
            writer.println("2 DATE " + DateManipulation.formatDate(partnership.getMarriageDate()));
        }

        final int father_id = partnership.getMalePartnerId();
        final int mother_id = partnership.getFemalePartnerId();

        writer.println("1 HUSB @" + individualLabel(father_id) + '@');
        writer.println("1 WIFE @" + individualLabel(mother_id) + '@');

        final List<Integer> child_ids = partnership.getChildIds();
        if (child_ids != null) {
            for (final int child_id : child_ids) {
                writer.println("1 CHIL @" + individualLabel(child_id) + '@');
            }
        }
    }

    @Override
    protected void outputTrailer(final PrintWriter writer) {

        writer.println("0 @S1@ SUBM\n" + "1 NAME " + SUBMITTER);
        writer.println("0 TRLR");
    }
}
