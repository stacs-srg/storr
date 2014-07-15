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
package uk.ac.standrews.cs.digitising_scotland.population_model.model.graphviz;

import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationToFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Writes a representation of the population to file in Graphviz format.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class PopulationToGraphviz extends PopulationToFile {

    // TODO implement IPopulationWriter

    private static final String ARC = " -> ";
    private static final String INDIVIDUAL_NODE_ATTRIBUTES = " [shape=box style=solid color=blue]";
    private static final String FAMILY_ARC_ATTRIBUTES = " [color=red arrowhead=none]";

    private final DateFormat formatter;
    private Collection<Integer> processed_partnerships;

    /**
     * Initialises the exporter. This includes potentially expensive scanning of the population graph.
     *
     * @param population  the population
     * @param path_string the path for the output file
     * @throws IOException if the file does not exist and cannot be created
     */
    public PopulationToGraphviz(final IPopulation population, final String path_string) {

        super(population, path_string);
        formatter = new SimpleDateFormat("dd/MM/yyyy");
        processed_partnerships = new HashSet<>();
    }

    @Override
    protected void outputHeader(final PrintWriter writer) {

        writer.println("digraph familyTree {");
        writer.println("node" + INDIVIDUAL_NODE_ATTRIBUTES);
    }

    @Override
    protected void outputIndividual(final PrintWriter writer, IPerson person) {

        writer.println(personLabel(person.getId()) + getIndividualNodeAttributes(person));

        List<Integer> partnership_ids = person.getPartnerships();
        if (partnership_ids != null) {
            for (final int partnership_id : partnership_ids) {

                if (!processed_partnerships.contains(partnership_id)) {

                    outputPartnership(writer, population.findPartnership(partnership_id));
                    processed_partnerships.add(partnership_id);
                }
            }
        }

        setRankIfOrphan(writer, person);
    }

    private void setRankIfOrphan(PrintWriter writer, IPerson person) {

        if (!personHasParents(person)) {
            final int id_of_next_person_with_parents = findIdOfClosestPersonWithParents(person);
            if (id_of_next_person_with_parents != -1) {
                writer.println("{ rank = same; " + personLabel(person.getId()) + " " + personLabel(id_of_next_person_with_parents) + "; }");
            }
        }
    }

    private int findIdOfClosestPersonWithParents(final IPerson person) {

        Iterator<IPerson> iterator = population.getPeople().iterator();

        // Advance to this person in the population, keeping track of most recent other person with parents.
        IPerson most_recent_with_parents = null;
        while (iterator.hasNext()) {
            IPerson next_person = iterator.next();
            if (next_person.getId() == person.getId()) break;
            if (personHasParents(next_person)) most_recent_with_parents = next_person;
        }

        while (iterator.hasNext()) {
            IPerson next_person = iterator.next();
            if (personHasParents(next_person)) return next_person.getId();
        }

        return most_recent_with_parents != null ? most_recent_with_parents.getId() : -1;
    }

    private boolean personHasParents(IPerson person) {

        int person_id = person.getId();
        for (IPartnership partnership : population.getPartnerships()) {
            List<Integer> child_ids = partnership.getChildIds();
            if (child_ids != null) {
                for (int child_id : child_ids) {
                    if (child_id == person_id) return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void outputFamilies(final PrintWriter writer) {

    }

    @Override
    protected void outputTrailer(final PrintWriter writer) {

        writer.println("}");
    }

    private String getIndividualNodeAttributes(final IPerson person) {

        final Date date_of_death = person.getDeathDate();

        StringBuilder builder = new StringBuilder();

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

        return " [shape=box color=red label=\"m: " + formatter.format(partnership.getMarriageDate()) + "\"]";
    }

    private void outputPartnership(final PrintWriter writer, final IPartnership partnership) {

        outputCouple(writer, partnership);
        outputChildren(writer, partnership);
    }

    private void outputCouple(final PrintWriter writer, final IPartnership partnership) {

        final int partnership_id = partnership.getId();
        final int partner1_id = partnership.getPartner1Id();
        final int partner2_id = partnership.getPartner2Id();

        writer.println(personLabel(partner1_id) + ARC + partnershipLabel(partnership_id) + FAMILY_ARC_ATTRIBUTES);
        writer.println(partnershipLabel(partnership_id) + ARC + personLabel(partner2_id) + FAMILY_ARC_ATTRIBUTES);
        writer.println(partnershipLabel(partnership_id) + getFamilyNodeAttributes(partnership));

        writer.println("{ rank = same; " + personLabel(partner1_id) + " " + personLabel(partner2_id) + " " + partnershipLabel(partnership_id) + "; }");
    }

    private String personLabel(int person_id) {
        return "p" + person_id;
    }

    private String partnershipLabel(int partnership_id) {
        return "m" + partnership_id;
    }

    private void outputChildren(final PrintWriter writer, final IPartnership partnership) {

        List<Integer> child_ids = partnership.getChildIds();
        if (child_ids != null) {

            final int partnership_id = partnership.getId();

            for (final int child_id : child_ids) {
                writer.println(partnershipLabel(partnership_id) + ARC + personLabel(child_id));
            }
        }
    }
}
