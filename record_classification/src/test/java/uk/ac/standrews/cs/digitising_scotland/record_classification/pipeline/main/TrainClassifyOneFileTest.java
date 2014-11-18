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

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

public class TrainClassifyOneFileTest {

    private TrainClassifyOneFile trainer;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testPipelineHisco() throws Exception, CodeNotValidException {

        String codeDictionary = getClass().getResource("/HiscoTitles.txt").getFile();
        MachineLearningConfiguration.getDefaultProperties().setProperty("codeDictionaryFile", codeDictionary);
        trainer = new TrainClassifyOneFile();
        String training = getClass().getResource("/occupationTrainingTest.txt").getFile();
        String propertiesFileLocation = "";
        String[] args = {training, propertiesFileLocation, "0.8", "0"};
        Bucket classified = trainer.run(args);
        Assert.assertEquals(20, classified.size(), 10);

    }

    @Test
    public void testPipelineCod() throws Exception, CodeNotValidException {

        String codeDictionary = getClass().getResource("/pilotTestCodeDictionary.txt").getFile();
        MachineLearningConfiguration.getDefaultProperties().setProperty("codeDictionaryFile", codeDictionary);
        trainer = new TrainClassifyOneFile();
        String training = getClass().getResource("/OneFileCodTestTrainingData.txt").getFile();
        String propertiesFileLocation = "src/test/resources/machineLearning.default.properties";
        String[] args = {training, propertiesFileLocation, "0.8", "1"};
        Bucket classified = trainer.run(args);
        Assert.assertEquals(20, classified.size(), 10);

    }

    @Test
    public void testRunNoGoldStandard() throws Exception, CodeNotValidException {

        trainer = new TrainClassifyOneFile();
        expectedEx.expect(RuntimeException.class);
        String[] args = {"testFile", "modelLoc"};
        trainer.run(args);

    }

    @Test
    public void testRunNoModelLocation() throws Exception, CodeNotValidException {

        trainer = new TrainClassifyOneFile();
        String goldStandardFile = getClass().getResource("/CauseOfDeathTestFileSmall.txt").getFile();
        expectedEx.expect(RuntimeException.class);
        String[] args = {goldStandardFile, "nonExistantModelLocation"};
        trainer.run(args);

    }

}
