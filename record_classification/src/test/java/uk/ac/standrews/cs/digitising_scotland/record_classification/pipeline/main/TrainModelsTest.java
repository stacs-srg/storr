/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.main;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;

public class TrainModelsTest {

    private TrainClassifyOneFile trainer;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws Exception {

        trainer = new TrainClassifyOneFile();
    }

    @Test
    public void testRunNoGoldStandard() throws Exception, CodeNotValidException {

        expectedEx.expect(RuntimeException.class);
        String[] args = {"testFile", "modelLoc"};
        trainer.run(args);

    }

    @Test
    public void testRunNoModelLocation() throws Exception, CodeNotValidException {

        String goldStandardFile = getClass().getResource("/CauseOfDeathTestFileSmall.txt").getFile();
        expectedEx.expect(RuntimeException.class);
        String[] args = {goldStandardFile, "nonExistantModelLocation"};
        trainer.run(args);

    }

}
