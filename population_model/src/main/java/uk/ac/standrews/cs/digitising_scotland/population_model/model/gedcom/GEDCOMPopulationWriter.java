/*
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
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

/**
 * Implementation of population export to GEDCOM format file.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
public class GEDCOMPopulationWriter extends AbstractFilePopulationWriter {

    private static final String CHAR_SET = "ASCII";
    private static final String GEDCOM_FORM = "LINEAGE-LINKED";
    private static final String GEDCOM_VERSION = "5.5.1";
    private static final String SOURCE_SOFTWARE = "Digitising_Scotland";
    private static final String SUBMITTER = "Digitising Scotland Project";
    private static final String SUBMITTER_ID = "@S1@";

    private static final String HEADER_TAG = "HEAD";
    private static final String TRAILER_TAG = "TRLR";
    private static final String SOURCE_TAG = "SOUR";
    private static final String SUBMITTER_TAG = "SUBM";
    private static final String GEDCOM_TAG = "GEDC";
    private static final String GEDCOM_VERSION_TAG = "VERS";
    private static final String GEDCOM_FORMAT_TAG = "FORM";
    private static final String CHARACTER_SET_TAG = "CHAR";
    private static final String INDIVIDUAL_TAG = "INDI";
    private static final String FAMILY_TAG = "FAM";
    private static final String MARRIAGE_TAG = "MARR";
    private static final String HUSBAND_TAG = "HUSB";
    private static final String WIFE_TAG = "WIFE";
    private static final String CHILD_TAG = "CHIL";
    private static final String NAME_TAG = "NAME";
    private static final String SEX_TAG = "SEX";
    private static final String BIRTH_TAG = "BIRT";
    private static final String DEATH_TAG = "DEAT";
    private static final String DATE_TAG = "DATE";
    private static final String PLACE_TAG = "PLAC";
    private static final String DEATH_CAUSE_TAG = "CAUS";
    private static final String OCCUPATION_TAG = "OCCU";
    private static final String FAMILY_AS_SPOUSE_TAG = "FAMS";
    private static final String FAMILY_AS_CHILD_TAG = "FAMC";

    private int level = 0;

    /**
     * Initialises the exporter. This includes potentially expensive scanning of the population graph.
     *
     * @param path the path for the output file
     * @throws IOException if the file does not exist and cannot be created
     */
    public GEDCOMPopulationWriter(final Path path) throws IOException {

        super(path);
    }

    @Override
    protected void outputHeader(final PrintWriter writer) {

        write(HEADER_TAG);
        incrementLevel();

        write(SOURCE_TAG, SOURCE_SOFTWARE);
        write(SUBMITTER_TAG, SUBMITTER_ID);

        write(GEDCOM_TAG);
        incrementLevel();
        write(GEDCOM_VERSION_TAG, GEDCOM_VERSION);
        write(GEDCOM_FORMAT_TAG, GEDCOM_FORM);
        decrementLevel();

        write(CHARACTER_SET_TAG, CHAR_SET);
        decrementLevel();
    }

    @SuppressWarnings("FeatureEnvy")
    @Override
    public void recordPerson(final IPerson person) {

        writeIndividualLabel(person);
        incrementLevel();

        writeName(person);
        writeSex(person);
        writeBirth(person);
        writeDeath(person);
        writeOccupation(person);
        writePartnerships(person);
        writeParentsPartnership(person);

        decrementLevel();
    }

    @SuppressWarnings("FeatureEnvy")
    @Override
    public void recordPartnership(final IPartnership partnership) {

        writeFamilyLabel(partnership);
        incrementLevel();

        writeMarriage(partnership);
        writeWife(partnership);
        writeHusband(partnership);
        writeChildren(partnership);

        decrementLevel();
    }

    @Override
    protected void outputTrailer(final PrintWriter writer) {

        write(SUBMITTER_ID + ' ' + SUBMITTER_TAG);
        incrementLevel();
        writeName(SUBMITTER);
        decrementLevel();
        write(TRAILER_TAG);
    }

    protected static int idToInt(final String id) {

        // Assume the string starts with an @ and another character, and ends with an @.
        return Integer.parseInt(id.substring(2, id.length() - 1));
    }

    private void writeIndividualLabel(final IPerson person) {

        write(makeIndividualReference(person.getId()) + ' ' + INDIVIDUAL_TAG);

    }

    private void writeFamilyLabel(final IPartnership partnership) {

        write(makeFamilyReference(partnership.getId()) + ' ' + FAMILY_TAG);
    }

    private void writeName(final String name) {

        write(NAME_TAG, name);
    }

    private void writeName(final IPerson person) {

        writeName(person.getFirstName() + " /" + person.getSurname() + '/');
    }

    private void writeSex(final IPerson person) {

        write(SEX_TAG, String.valueOf(person.getSex()));
    }

    private void writeBirth(final IPerson person) {

        final Date birth_date = person.getBirthDate();
        if (birth_date != null) {

            write(BIRTH_TAG);
            incrementLevel();

            writeDate(birth_date);
            writePlace(person.getBirthPlace());

            decrementLevel();
        }
    }

    @SuppressWarnings("FeatureEnvy")
    private void writeDeath(final IPerson person) {

        final Date death_date = person.getDeathDate();
        if (death_date != null) {

            write(DEATH_TAG);
            incrementLevel();

            writeDate(death_date);
            writePlace(person.getDeathPlace());
            writeDeathCause(person.getDeathCause());

            decrementLevel();
        }
    }

    private void writeMarriage(final IPartnership partnership) {

        final Date marriage_date = partnership.getMarriageDate();
        if (marriage_date != null) {

            write(MARRIAGE_TAG);
            incrementLevel();

            writeDate(marriage_date);
            writePlace(partnership.getMarriagePlace());

            decrementLevel();
        }
    }

    private void writeDate(final Date date) {

        write(DATE_TAG, DateManipulation.formatDate(date));
    }

    private void writePlace(final String place) {

        checkAndWrite(PLACE_TAG, place);
    }

    private void writeDeathCause(final String cause) {

        checkAndWrite(DEATH_CAUSE_TAG, cause);
    }

    private void writeOccupation(final String occupation) {

        checkAndWrite(OCCUPATION_TAG, occupation);
    }

    private void checkAndWrite(final String tag, final String data) {

        if (data != null) {
            write(tag, data);
        }
    }

    private void writeOccupation(final IPerson person) {

        writeOccupation(person.getOccupation());
    }

    private void writePartnerships(final IPerson person) {

        final List<Integer> partnership_ids = person.getPartnerships();

        if (partnership_ids != null) {
            for (final int partnership_id : partnership_ids) {
                write(FAMILY_AS_SPOUSE_TAG, makeFamilyReference(partnership_id));
            }
        }
    }

    private void writeParentsPartnership(final IPerson person) {

        final int parents_partnership_id = person.getParentsPartnership();
        if (parents_partnership_id != -1) {
            write(FAMILY_AS_CHILD_TAG, makeFamilyReference(parents_partnership_id));

        }
    }

    private void writeChildren(final IPartnership partnership) {

        final List<Integer> child_ids = partnership.getChildIds();
        if (child_ids != null) {
            for (final int child_id : child_ids) {
                writeChild(child_id);
            }
        }
    }

    private void writeWife(final IPartnership partnership) {

        write(WIFE_TAG, makeIndividualReference(partnership.getFemalePartnerId()));
    }

    private void writeHusband(final IPartnership partnership) {

        write(HUSBAND_TAG, makeIndividualReference(partnership.getMalePartnerId()));
    }

    private void writeChild(final int child_id) {

        write(CHILD_TAG, makeIndividualReference(child_id));
    }

    private static String makeIndividualReference(final int id) {

        return makeReference(individualLabel(id));
    }

    private static String makeFamilyReference(final int id) {

        return makeReference(familyLabel(id));
    }

    private static String makeReference(final String label) {

        return '@' + label + '@';
    }

    private void incrementLevel() {
        level++;
    }

    private void decrementLevel() {
        level--;
    }

    private void write(final String tag, final String data) {

        write(tag + ' ' + data);
    }

    private void write(final String data) {

        writer.println(level + " " + data);
    }
}
