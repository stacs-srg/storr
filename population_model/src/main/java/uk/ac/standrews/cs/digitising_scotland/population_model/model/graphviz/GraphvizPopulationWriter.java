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
package uk.ac.standrews.cs.digitising_scotland.population_model.model.graphviz;

import uk.ac.standrews.cs.digitising_scotland.population_model.model.AbstractFilePopulationWriter;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Writes a representation of the population to file in Graphviz format.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class GraphvizPopulationWriter extends AbstractFilePopulationWriter {

    private static final String INDIVIDUAL_NODE_COLOUR = "blue";
    private static final String PARTNERSHIP_NODE_COLOUR = "red";
    private static final String PARTNERSHIP_ARC_COLOUR = "red";

    private static final String ARC = " -> ";
    private static final String INDIVIDUAL_NODE_ATTRIBUTES = " [shape=box style=solid color=" + INDIVIDUAL_NODE_COLOUR + ']';
    private static final String FAMILY_ARC_ATTRIBUTES = " [color=" + PARTNERSHIP_ARC_COLOUR + " arrowhead=none]";

    private final DateFormat formatter;
    private final IPopulation population;

    /**
     * Initialises the writer.
     *
     * @param population the population to be written
     * @param path the path for the output file
     * @throws IOException if the file does not exist and cannot be created
     */
     public GraphvizPopulationWriter(final IPopulation population, final Path path) throws IOException {

        super(path);
        this.population = population;

        formatter = new SimpleDateFormat("dd/MM/yyyy");
    }

    @Override
    protected void outputHeader(final PrintWriter writer) {

        writer.println("digraph familyTree {");
        writer.println("node" + INDIVIDUAL_NODE_ATTRIBUTES);
    }

    @Override
    public void recordPerson(final IPerson person) {

        writer.println(individualLabel(person.getId()) + getIndividualNodeAttributes(person));
        setRankIfOrphan(person);
    }

    @Override
    public void recordPartnership(final IPartnership partnership) {

        outputCouple(partnership);
        outputChildren(partnership);
    }

    @Override
    protected void outputTrailer(final PrintWriter writer) {

        writer.println("}");
    }

    @SuppressWarnings("FeatureEnvy")
    private void outputCouple(final IPartnership partnership) {

        final int partnership_id = partnership.getId();
        final int female_partner_id = partnership.getFemalePartnerId();
        final int male_partner_id = partnership.getMalePartnerId();

        writer.println(individualLabel(female_partner_id) + ARC + familyLabel(partnership_id) + FAMILY_ARC_ATTRIBUTES);
        writer.println(familyLabel(partnership_id) + ARC + individualLabel(male_partner_id) + FAMILY_ARC_ATTRIBUTES);
        writer.println(familyLabel(partnership_id) + getFamilyNodeAttributes(partnership));

        writer.println("{ rank = same; " + individualLabel(female_partner_id) + ' ' + individualLabel(male_partner_id) + ' ' + familyLabel(partnership_id) + "; }");
    }

    private void outputChildren(final IPartnership partnership) {

        final List<Integer> child_ids = partnership.getChildIds();
        if (child_ids != null) {

            final int partnership_id = partnership.getId();

            for (final int child_id : child_ids) {
                writer.println(familyLabel(partnership_id) + ARC + individualLabel(child_id));
            }
        }
    }

    private String getIndividualNodeAttributes(final IPerson person) {

        final Date date_of_death = person.getDeathDate();

        final StringBuilder builder = new StringBuilder();

        builder.append(" [label=\"b: ");
        builder.append(formatter.format(person.getBirthDate()));
        if (date_of_death != null) {
            builder.append("\\nd: ");
            builder.append(formatter.format(date_of_death));
        }
        builder.append("\"]");

        return builder.toString();
    }

    private String getFamilyNodeAttributes(final IPartnership partnership) {

        return " [shape=box color=" + PARTNERSHIP_NODE_COLOUR + " label=\"m: " + formatter.format(partnership.getMarriageDate()) + "\"]";
    }

    private void setRankIfOrphan(final IPerson person) {

        if (!personHasParents(person)) {
            final int id_of_next_person_with_parents = findIdOfClosestPersonWithParents(person);
            if (id_of_next_person_with_parents != -1) {
                writer.println("{ rank = same; " + individualLabel(person.getId()) + ' ' + individualLabel(id_of_next_person_with_parents) + "; }");
            }
        }
    }

    @SuppressWarnings("FeatureEnvy")
    private int findIdOfClosestPersonWithParents(final IPerson person) {

        final Iterator<IPerson> iterator = population.getPeople().iterator();

        // Advance to this person in the population, keeping track of most recent other person with parents.
        IPerson most_recent_with_parents = null;
        while (iterator.hasNext()) {
            final IPerson next_person = iterator.next();
            if (next_person.getId() == person.getId()) {
                break;
            }
            if (personHasParents(next_person)) {
                most_recent_with_parents = next_person;
            }
        }

        while (iterator.hasNext()) {
            final IPerson next_person = iterator.next();
            if (personHasParents(next_person)) {
                return next_person.getId();
            }
        }

        return most_recent_with_parents != null ? most_recent_with_parents.getId() : -1;
    }

    private boolean personHasParents(final IPerson person) {

        final int person_id = person.getId();
        for (final IPartnership partnership : population.getPartnerships()) {
            final List<Integer> child_ids = partnership.getChildIds();
            if (child_ids != null) {
                for (final int child_id : child_ids) {
                    if (child_id == person_id) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
