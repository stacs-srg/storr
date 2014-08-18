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

import org.gedcom4j.model.Family;
import org.gedcom4j.model.FamilyEvent;
import org.gedcom4j.model.Individual;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.AbstractPartnership;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Partnership implementation for a population represented in a GEDCOM file.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk>
 */
public class GEDCOMPartnership extends AbstractPartnership {

    /**
     * Initialises the partnership.
     *
     * @param family the GEDCOM family representation
     * @throws ParseException if the marriage date is incorrectly formatted
     */
    public GEDCOMPartnership(final Family family) throws ParseException {

        id = getId(family.xref);
        male_partner_id = getId(family.husband.xref);
        female_partner_id = getId(family.wife.xref);

        setMarriage(family);
        setChildren(family);
    }

    private static int getId(final String reference) {

        return GEDCOMPopulationWriter.idToInt(reference);
    }

    private void setMarriage(final Family family) throws ParseException {

        for (final FamilyEvent event : family.events) {

            switch (event.type) {

                case MARRIAGE:
                    marriage_date = DateManipulation.parseDate(event.date.toString());
                    marriage_place = event.place.placeName;
                    break;

                default:
                    break;
            }
        }
    }

    private void setChildren(final Family family) {

        child_ids = new ArrayList<>();
        for (final Individual child : family.children) {
            child_ids.add(GEDCOMPopulationWriter.idToInt(child.xref));
        }
    }
}
