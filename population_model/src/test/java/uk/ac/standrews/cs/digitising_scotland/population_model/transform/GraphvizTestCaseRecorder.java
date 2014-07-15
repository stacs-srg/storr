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
package uk.ac.standrews.cs.digitising_scotland.population_model.transform;

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.InconsistentWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.NegativeDeviationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.NegativeWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationToFile;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.graphviz.PopulationToGraphviz;

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
    protected PopulationToFile getExporter(final IPopulation population, final String path_string) throws IOException, InconsistentWeightException {

        return new PopulationToGraphviz(population, path_string);
    }

    @Override
    protected String getIntendedOutputFileSuffix() {

        return PopulationToGraphvizTest.INTENDED_SUFFIX;
    }
}
