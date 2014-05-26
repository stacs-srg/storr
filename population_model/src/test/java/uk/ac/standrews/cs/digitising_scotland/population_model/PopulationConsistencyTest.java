package uk.ac.standrews.cs.digitising_scotland.population_model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeDeviationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPopulation;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Tests of various population properties.
 * 
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
@RunWith(Parameterized.class)
public class PopulationConsistencyTest {

    private static final long SEED = 534534524234234L;

    private CompactPopulation population;
    private final int population_size;

    @Parameters
    public static Collection<Object[]> generateData() {

        return Arrays.asList(new Object[][]{{1}, {10}, {100}, {1000}});
    }

    public PopulationConsistencyTest(final int population_size) {

        this.population_size = population_size;
    }

    @Before
    public void setup() throws IOException, NegativeDeviationException, NegativeWeightException {

        population = new CompactPopulation(population_size);
    }

    @Test
    public void populationSize() {

        assertEquals(population_size, population.getPeople().length);
    }

    @Test
    public void parentsHaveSensibleAgesAtChildBirth() {

        assertParentsHaveSensibleAgesAtChildBirth(population);
    }

    @Test
    public void noSiblingsMarried() {

        assertNoSiblingsMarried(population);
    }

    @Test
    public void noParentsMarriedToChildren() {

        assertNoParentsMarriedToChildren(population);
    }

    @Test
    public void noSameSexMarriages() {

        assertNoSameSexMarriages(population);
    }

    private void assertParentsHaveSensibleAgesAtChildBirth(final CompactPopulation population) {

        for (final CompactPerson p : population.getPeople()) {

            if (p.getPartnerships() != null) {
                for (final CompactPartnership partnership : p.getPartnerships()) {

                    for (final int child : partnership.getChildren()) {
                        assertTrue(population.parentsHaveSensibleAgesAtChildBirth(partnership, child));
                    }
                }
            }
        }
    }

    private void assertNoSiblingsMarried(final CompactPopulation population) {

        for (final CompactPerson p : population.getPeople()) {

            // Include half-siblings.
            final Set<Integer> siblings = new HashSet<>();

            if (p.getPartnerships() != null) {
                for (final CompactPartnership partnership : p.getPartnerships()) {

                    for (final int child_index : partnership.getChildren()) {

                        assertNotMarriedToAnyOf(child_index, siblings);
                        siblings.add(child_index);
                    }
                }
            }
        }
    }

    private void assertNotMarriedToAnyOf(final int p, final Set<Integer> people) {

        assertTrue(notMarriedToAnyOf(p, people));
    }

    protected boolean notMarriedToAnyOf(final int p, final Set<Integer> people) {

        for (final int p2 : people) {
            if (population.married(p, p2)) { return false; }
        }

        return true;
    }

    private void assertNoParentsMarriedToChildren(final CompactPopulation population) {

        for (final CompactPerson p : population.getPeople()) {

            if (p.getPartnerships() != null) {
                for (final CompactPartnership partnership : p.getPartnerships()) {

                    for (final int child : partnership.getChildren()) {

                        assertNotSame(child, partnership.getPartner1());
                        assertNotSame(child, partnership.getPartner2());
                    }
                }
            }
        }
    }

    private void assertNoSameSexMarriages(final CompactPopulation population) {

        for (final CompactPerson p : population.getPeople()) {

            if (p.getPartnerships() != null) {
                for (final CompactPartnership partnership : p.getPartnerships()) {

                    CompactPerson[] people = population.getPeople();
                    assertTrue(CompactPerson.oppositeSex(people[partnership.getPartner1()], people[partnership.getPartner2()]));
                }
            }
        }
    }
}
