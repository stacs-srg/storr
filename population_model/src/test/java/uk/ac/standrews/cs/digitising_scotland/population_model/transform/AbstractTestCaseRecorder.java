package uk.ac.standrews.cs.digitising_scotland.population_model.transform;

import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.InconsistentWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeDeviationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IDFactory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Tests of Graphviz export.
 * 
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public abstract class AbstractTestCaseRecorder {

    protected void recordTestCase() throws IOException, InconsistentWeightException, NegativeDeviationException, NegativeWeightException {

        for (int i = 0; i < AbstractExporterTest.TEST_CASE_POPULATION_SIZES.length; i++) {

            IDFactory.resetId();

            final String path_string = Paths.get(AbstractExporterTest.TEST_DIRECTORY_PATH_STRING, AbstractExporterTest.TEST_CASE_FILE_NAME_ROOTS[i] + getIntendedOutputFileSuffix()).toString();

            final CompactPopulation population = new CompactPopulation(AbstractExporterTest.TEST_CASE_POPULATION_SIZES[i]);

            final PopulationToFile exporter = getExporter(population, path_string);

            exporter.export();
        }
    }

    protected abstract String getIntendedOutputFileSuffix();

    protected abstract PopulationToFile getExporter(CompactPopulation population, String path_string) throws IOException, InconsistentWeightException;
}
