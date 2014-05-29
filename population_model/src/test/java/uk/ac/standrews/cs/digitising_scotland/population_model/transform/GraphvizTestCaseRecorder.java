package uk.ac.standrews.cs.digitising_scotland.population_model.transform;

import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.InconsistentWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeDeviationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPopulation;

import java.io.IOException;

/**
 * Generates test cases for Graphviz export.
 * 
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class GraphvizTestCaseRecorder extends AbstractTestCaseRecorder {

    public static void main(final String[] args) throws IOException, InconsistentWeightException, NegativeDeviationException, NegativeWeightException {

        final GraphvizTestCaseRecorder recorder = new GraphvizTestCaseRecorder();
        recorder.recordTestCase();
    }

    @Override
    protected PopulationToFile getExporter(final CompactPopulation population, final String path_string) throws IOException, InconsistentWeightException {

        return new PopulationToGraphviz(population, path_string);
    }

    @Override
    protected String getIntendedOutputFileSuffix() {

        return PopulationToGraphvizTest.INTENDED_SUFFIX;
    }
}
