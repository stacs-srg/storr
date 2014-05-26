package uk.ac.standrews.cs.digitising_scotland.population_model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.InconsistentWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.transform.PopulationToGEDCOM;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Tests of GEDCOM export.
 * 
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
@RunWith(Parameterized.class)
public class PopulationToGEDCOMTest extends AbstractExporterTest {

    protected static final String INTENDED_SUFFIX = "_intended.ged";
    private static final String ACTUAL_SUFFIX = "_test.ged";

    public PopulationToGEDCOMTest(final CompactPopulation population, final String file_name) {

        super(population, file_name);
    }

    @Test
    public void test() throws IOException, InconsistentWeightException {

        final Path actual_output = Paths.get(TEST_DIRECTORY_PATH_STRING, file_name_root + ACTUAL_SUFFIX);
        final Path intended_output = Paths.get(TEST_DIRECTORY_PATH_STRING, file_name_root + INTENDED_SUFFIX);

        final PopulationToGEDCOM exporter = new PopulationToGEDCOM(population, actual_output.toString());
        exporter.export();
        assertThatFilesHaveSameContent(actual_output, intended_output);
    }
}
