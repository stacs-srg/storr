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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.RecordFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

/**
 * Test class to test {@link ExactMatchClassifier}.
 * @author jkc25
 *
 */
public class ExactMatchClassifierTest {

    /** The training bucket. */
    private Bucket trainingBucket;

    /** The testing bucket. */
    private Bucket testingBucket;

    /** The exact match classifier. */
    private ExactMatchClassifier exactMatchClassifier;

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {

        trainingBucket = getTrainingBucket();
        testingBucket = createTestingBucket();
        train();

    }

    @After
    public void tearDown() {

        FileUtils.deleteQuietly(new File("target/lookupTable.ser"));
        FileUtils.deleteQuietly(new File("target/exactmatchlookuptable.ser"));

    }

    /**
     * Test train.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTrain() throws Exception {

        train();
        Assert.assertNotNull(exactMatchClassifier.toString());

    }

    /**
     * Train.
     *
     * @return the exact match classifier
     * @throws Exception the exception
     */
    private ExactMatchClassifier train() throws Exception {

        exactMatchClassifier = new ExactMatchClassifier();
        exactMatchClassifier.train(trainingBucket);
        return exactMatchClassifier;
    }

    /**
     * Creates the testing bucket.
     *
     * @return the bucket
     * @throws Exception the exception
     */
    private Bucket createTestingBucket() throws Exception {

        Bucket trainingBucket;
        File inputFileTraining = new File(getClass().getResource("/occupationTestFormatPipeTesting.txt").getFile());
        List<Record> listOfRecordsTraining = RecordFactory.makeUnCodedRecordsFromFile(inputFileTraining);
        trainingBucket = new Bucket(listOfRecordsTraining);

        return trainingBucket;
    }

    /**
     * Gets the training bucket.
     *
     * @return the training bucket
     * @throws Exception the exception
     */
    public Bucket getTrainingBucket() throws Exception {

        Bucket trainingBucket;
        File inputFileTraining = new File(getClass().getResource("/occupationTestFormatPipe.txt").getFile());
        List<Record> listOfRecordsTraining = RecordFactory.makeUnCodedRecordsFromFile(inputFileTraining);
        trainingBucket = new Bucket(listOfRecordsTraining);

        return trainingBucket;
    }

    /**
     * Serialization write test.
     *
     * @throws Exception the exception
     */
    @Test
    public void serializationWriteTest() throws Exception {

        exactMatchClassifier = train();
        exactMatchClassifier.writeModel("target/exactmatchlookuptable");
        Assert.assertTrue(new File("target/exactmatchlookuptable.ser").exists());
    }

    /**
     * Serialization read test.
     *
     * @throws Exception the exception
     */
    @Test
    public void serializationReadTest() throws Exception {

        exactMatchClassifier = train();
        exactMatchClassifier.writeModel("target/exactmatchlookuptable");
        Assert.assertTrue(new File("target/exactmatchlookuptable.ser").exists());
        ExactMatchClassifier newMatcher = new ExactMatchClassifier();
        newMatcher.readModel("target/exactmatchlookuptable");
        Assert.assertEquals(exactMatchClassifier, newMatcher);
    }

    @Test
    public void blackListTest() throws IOException {

        ExactMatchClassifier classifier = new ExactMatchClassifier();
        List<String> blacklist = new ArrayList<>();
        String concatDescription = "foo";
        Set<Classification> goldStandardCodes = new HashSet<>();
        Map<String, Set<Classification>> lookup = new HashMap<>();
        classifier.addToLookup(lookup, goldStandardCodes, concatDescription, blacklist);
        Assert.assertEquals(1, lookup.size());
        Assert.assertEquals(0, blacklist.size());

        concatDescription = "foo bar";
        classifier.addToLookup(lookup, goldStandardCodes, concatDescription, blacklist);
        Assert.assertEquals(2, lookup.size());
        Assert.assertEquals(0, blacklist.size());

        concatDescription = "foo";
        Set<Classification> set2 = makeClassificationSet();
        classifier.addToLookup(lookup, set2, concatDescription, blacklist);
        Assert.assertEquals(1, lookup.size());
        Assert.assertEquals(1, blacklist.size());

    }

    private Set<Classification> makeClassificationSet() throws IOException {

        File codeDictionaryFile = new File(getClass().getResource("/CodeCheckerTest.txt").getFile());
        CodeDictionary dictionary = new CodeDictionary(codeDictionaryFile);
        Code code = dictionary.getIterator().next().getValue();
        TokenSet tokenSet = new TokenSet("test");
        Double confidence = 1.0;
        Classification c = new Classification(code, tokenSet, confidence);
        Set<Classification> set = new HashSet<>();
        set.add(c);
        return set;
    }
}
