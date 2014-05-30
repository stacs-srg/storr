package uk.ac.standrews.cs.digitising_scotland.population_model.transform;

import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.InconsistentWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.Person;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Writes a representation of the population to file in Graphviz format.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class PopulationToGraphviz extends PopulationToFile {

    private static final String ARC = " -> ";
    private static final String INDIVIDUAL_NODE_ATTRIBUTES = " [shape=box style=solid color=blue]";
    private static final String FAMILY_ARC_ATTRIBUTES = " [color=red arrowhead=none]";

    private final DateFormat formatter;

    /**
     * Initialises the exporter. This includes potentially expensive scanning of the population graph.
     *
     * @param population the population
     * @param path_string       the path for the output file
     * @throws IOException if the file does not exist and cannot be created
     */
    public PopulationToGraphviz(final CompactPopulation population, final String path_string) throws IOException, InconsistentWeightException {

        super(population, path_string);
        formatter = new SimpleDateFormat("dd/MM/yyyy");
    }

    @Override
    protected void outputHeader(final PrintWriter writer) {

        writer.println("digraph familyTree {");
        writer.println("node" + INDIVIDUAL_NODE_ATTRIBUTES);
    }

    @Override
    protected void outputIndividual(final PrintWriter writer, final int index, final CompactPerson compact_person, final Person person) {

        writer.println(compact_person.getId() + getIndividualNodeAttributes(compact_person));

        if (compact_person.getPartnerships() != null) {
            for (final CompactPartnership partnership : compact_person.getPartnerships()) {

                if (!processed_partnerships.contains(partnership)) {

                    outputPartnership(writer, partnership);
                    processed_partnerships.add(partnership);
                }
            }
        }

        if (!compact_person.hasParents()) {
            final CompactPerson next_person_with_parents = findNextPersonWithParents(compact_person);
            if (next_person_with_parents != null) {
                final String person_in_same_generation_id = padId(next_person_with_parents.getId());
                writer.println("{ rank = same; " + compact_person.getId() + " " + person_in_same_generation_id + "; }");
            }
        }
    }

    private CompactPerson findNextPersonWithParents(final CompactPerson person) {

        int index = population.findPerson(person);

        final CompactPopulation.Condition has_parents_condition = new CompactPopulation.Condition() {

            @Override
            public boolean check(final int person_index) {

                return population.getPerson(person_index).hasParents();
            }
        };

        int index_of_person_with_parents = population.findPerson(index + 1, has_parents_condition);
        return index_of_person_with_parents > -1 ? population.getPerson(index_of_person_with_parents) : null;
    }

    @Override
    protected void outputFamilies(final PrintWriter writer) {

    }

    @Override
    protected void outputTrailer(final PrintWriter writer) {

        writer.println("}");
    }

    private String getIndividualNodeAttributes(final CompactPerson person) {

        final int date_of_death = person.getDateOfDeath();

        StringBuilder builder = new StringBuilder();

        builder.append(" [label=\"b: ");
        builder.append(formatter.format(DateManipulation.daysToDate(person.getDateOfBirth())));
        if (date_of_death > -1) {
            builder.append("\\nd: ");
            builder.append(formatter.format(DateManipulation.daysToDate(date_of_death)));
        }
        builder.append("\"]");

        return builder.toString();
    }

    private String getFamilyNodeAttributes(final CompactPartnership partnership) {

        return " [shape=box color=red label=\"m: " + formatter.format(DateManipulation.daysToDate(partnership.getMarriageDate())) + "\"]";
    }

    private void outputPartnership(final PrintWriter writer, final CompactPartnership partnership) {

        final int partnership_id = partnership.getId();
        final int partner1_id = partnership.getPartner1();
        final int partner2_id = partnership.getPartner2();

        outputCouple(writer, partnership_id, partner1_id, partner2_id, partnership);
        outputChildren(writer, partnership_id, partnership);
    }

    private void outputCouple(final PrintWriter writer, final int partnership_id, final int partner1_id, final int partner2_id, final CompactPartnership partnership) {

        final String link_target = ARC + partnership_id + FAMILY_ARC_ATTRIBUTES;

        writer.println(partner1_id + link_target);
        writer.println(partner2_id + link_target);
        writer.println(partnership_id + getFamilyNodeAttributes(partnership));

        writer.println("{ rank = same; " + partner1_id + " " + partner2_id + " " + partnership_id + "; }");
    }

    private void outputChildren(final PrintWriter writer, final int partnership_id, final CompactPartnership partnership) {

        if (partnership.getChildren() != null) {
            for (final int child : partnership.getChildren()) {
                if (child != -1) {
                    writer.println(partnership_id + ARC + child);
                }
            }
        }
    }
}
