/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.writers;

import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.CODOrignalData;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;

/**
 * The Class OutputDataFormatter formats the fields of a record into a readable format for either manual analysis or
 * data clerking.
 * Methods should be overridden to give desired behaviour, though the default should still be useful.
 * The delimiter can be set by calling the setDelimiter method, otherwise the default of | (pipe) is used.
 * 
 */
public class OutputDataFormatter {

    /** The delimiter. */
    private String delimiter = "|";

    /**
     * Gets the image quality and appends with the delimiter.
     *
     * @param record the record
     * @return the image quality
     */
    public String getImageQuality(final Record record) {

        return String.valueOf(record.getOriginalData().getImageQuality()) + delimiter;
    }

    /**
     * Gets the year and appends with the delimiter.
     *
     * @param record the record
     * @return the year
     */
    public String getYear(final Record record) {

        return String.valueOf(record.getOriginalData().getYear()) + delimiter;
    }

    /**
     * Gets the description and appends with the delimiter.
     *
     * @param record the record
     * @return the description
     */
    public String getDescription(final Record record) {

        return record.getOriginalData().getDescription() + delimiter;
    }

    /**
     * Gets the codes and appends each one with the delimiter.
     * @param description The description to get the codes for
     * @param record the record
     * @return the codes
     */
    public String getCodes(final Record record, final String description) {

        StringBuilder sb = new StringBuilder();

        Set<Classification> ct = record.getListOfClassifications().get(description);
        for (Classification codeTriple : ct) {

            Code code = codeTriple.getCode();
            String codeAsString = code.getCodeAsString();
            String tokenSet = codeTriple.getTokenSet().toString();
            sb.append(tokenSet + delimiter + codeAsString + delimiter);

        }

        return sb.toString();
    }

    /**
     * Gets the description and appends it with the delimiter.
     *
     * @param record the record
     * @param code the code
     * @return the description
     */
    public String getDesciption(final Record record, final Code code) {

        String description;

        if (record.isCoDRecord()) {
            description = code.getDescription();
        }
        else {
            description = "Classified into HISCO";
        }
        return description;
    }

    /**
     * Gets the explanation and appends it with the delimiter.
     *
     * @param codeAsString the code as string
     * @return the explanation
     */
    public String getExplanation(final String codeAsString) {

        final int expectedLength = 5;
        if (codeAsString.length() == expectedLength && !String.valueOf(codeAsString.charAt(4)).equals("0")) { return "Coded to the extended historical version (codes ending .01 to .09)"; }
        return "";
    }

    /**
     * Gets the age group and appends it with the delimiter.
     *
     * @param record the record
     * @return the age group
     */
    public String getAgeGroup(final Record record) {

        if (!record.isCoDRecord()) { return ""; }
        CODOrignalData originalData = (CODOrignalData) record.getOriginalData();
        int ageGroup = originalData.getAgeGroup();

        String ageGroupString;

        switch (ageGroup) {
            case 0:
                ageGroupString = "(" + 0 + ") 0 - 1";
                break;
            case 1:
                ageGroupString = "(" + 1 + ") 2 - 5";
                break;
            case 2:
                ageGroupString = "(" + 2 + ") 6 - 10";
                break;
            case 3:
                ageGroupString = "(" + 3 + ") 11 - 15";
                break;
            case 4:
                ageGroupString = "(" + 4 + ") 16 - 45";
                break;
            default:
                ageGroupString = "(" + 5 + ") 56+";
        }
        return ageGroupString + delimiter;
    }

    /**
     * Gets the sex and appends it with the delimiter.
     *
     * @param record the record
     * @return the sex
     */
    public String getSex(final Record record) {

        if (!record.isCoDRecord()) { return ""; }
        CODOrignalData originalData = (CODOrignalData) record.getOriginalData();
        int sex = originalData.getSex();
        if (sex == 0) { return "F" + delimiter; }
        return "M" + delimiter;

    }

    /**
     * Gets the id and appends it with the delimiter.
     *
     * @param record the record
     * @return the id
     */
    public String getID(final Record record) {

        String id = String.valueOf(record.getid()) + delimiter;
        return id;
    }

    /**
     * Sets the delimiter.
     *
     * @param delimiter the new delimiter
     */
    public void setDelimier(final String delimiter) {

        this.delimiter = delimiter;
    }

    /**
     * Gets the delimiter.
     *
     * @return the delimiter
     */
    public String getDelimiter() {

        return delimiter;
    }
}
