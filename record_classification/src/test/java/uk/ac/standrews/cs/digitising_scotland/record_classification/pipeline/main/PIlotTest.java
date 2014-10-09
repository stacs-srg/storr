package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.main;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

public class PIlotTest {

    PIlot pilot;

    @Before
    public void setUp() throws Exception {

        pilot = new PIlot();
        String codeDictionaryLocation = getClass().getResource("/pilotTestCodeDictionary.txt").getFile();
        MachineLearningConfiguration.getDefaultProperties().setProperty("codeDictionaryFile", codeDictionaryLocation);
    }

    @Test
    public void test() throws Exception {

        Iterator<Classification> it;
        Set<String> codesinmap;
        String trainingData = getClass().getResource("/PilotTestTrainingData.txt").getFile();
        String testData = getClass().getResource("/pilotTest.tsv").getFile();
        String[] args = {trainingData, testData};

        Bucket allClassified = pilot.run(args);

        Set<Classification> classifications = allClassified.getRecord(46999).getClassifications();
        System.out.println(classifications);
        Assert.assertEquals(3, classifications.size());
        it = classifications.iterator();
        codesinmap = getCodesInMap(it);
        Assert.assertTrue(codesinmap.contains("I340"));
        Assert.assertTrue(codesinmap.contains("I48"));
        Assert.assertTrue(codesinmap.contains("I515"));

        classifications = allClassified.getRecord(72408).getClassifications();
        System.out.println(classifications);
        Assert.assertEquals(1, classifications.size());
        it = classifications.iterator();
        codesinmap = getCodesInMap(it);
        Assert.assertTrue(codesinmap.contains("I340"));

        classifications = allClassified.getRecord(6804).getClassifications();
        System.out.println(classifications);
        Assert.assertEquals(2, classifications.size());
        it = classifications.iterator();
        codesinmap = getCodesInMap(it);
        Assert.assertTrue(codesinmap.contains("I219"));
        Assert.assertTrue(codesinmap.contains("I515"));

        classifications = allClassified.getRecord(43454).getClassifications();
        System.out.println(classifications);
        Assert.assertEquals(1, classifications.size());
        it = classifications.iterator();
        codesinmap = getCodesInMap(it);
        Assert.assertTrue(codesinmap.contains("I219"));

        classifications = allClassified.getRecord(6809).getClassifications();
        System.out.println(classifications);
        Assert.assertEquals(2, classifications.size());
        it = classifications.iterator();
        codesinmap = getCodesInMap(it);
        Assert.assertTrue(codesinmap.contains("I219"));
        Assert.assertTrue(codesinmap.contains("I515"));

    }

    private Set<String> getCodesInMap(Iterator<Classification> it) {

        Set<String> codesinmap = new HashSet<>();

        while (it.hasNext()) {
            Classification classification = (Classification) it.next();
            codesinmap.add(classification.getCode().getCodeAsString());
        }
        return codesinmap;
    }
}
