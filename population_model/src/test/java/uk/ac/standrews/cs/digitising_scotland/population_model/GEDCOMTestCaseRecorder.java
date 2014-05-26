package uk.ac.standrews.cs.digitising_scotland.population_model;

import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.InconsistentWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeDeviationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.transform.PopulationToFile;
import uk.ac.standrews.cs.digitising_scotland.population_model.transform.PopulationToGEDCOM;

import java.io.IOException;

/**
 * Generates test cases for GEDCOM export.
 * 
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class GEDCOMTestCaseRecorder extends AbstractTestCaseRecorder {

    public static void main(final String[] args) throws IOException, InconsistentWeightException, NegativeDeviationException, NegativeWeightException {

        final GEDCOMTestCaseRecorder recorder = new GEDCOMTestCaseRecorder();

        recorder.recordTestCase();
    }

    @Override
    protected PopulationToFile getExporter(final CompactPopulation population, final String path_string) throws IOException, InconsistentWeightException {

        return new PopulationToGEDCOM(population, path_string);
    }

    @Override
    protected String getIntendedOutputFileSuffix() {

        return PopulationToGEDCOMTest.INTENDED_SUFFIX;
    }
}
