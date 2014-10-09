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

/**
 * Test class for the {@link PIlot} class. Exact matches and trains an OLR with known input and output.
 * Should test all types of classification with exact match and olr.
 * ie, fully matched, half matched half learned, not matched.
 * @author jkc25
 *
 */
public class PIlotTest {

    private PIlot pilot;

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

        Bucket allRecords = pilot.run(args);

        final int numberOfRecords = 7;
        Assert.assertTrue(allRecords.size() == numberOfRecords);

        Set<Classification> classifications = allRecords.getRecord(46999).getClassifications();
        System.out.println(classifications);
        Assert.assertEquals(3, classifications.size());
        it = classifications.iterator();
        codesinmap = getCodesInMap(it);
        Assert.assertTrue(codesinmap.contains("I340"));
        Assert.assertTrue(codesinmap.contains("I48"));
        Assert.assertTrue(codesinmap.contains("I515"));
        final Set<Classification> sterosisSet = allRecords.getRecord(46999).getListOfClassifications().get("mitral sterosis");
        Assert.assertTrue(sterosisSet.size() == 1);
        Assert.assertTrue(sterosisSet.iterator().next().getConfidence() < 1);
        final Set<Classification> myocardialSet = allRecords.getRecord(46999).getListOfClassifications().get("myocardial degeneration");
        Assert.assertTrue(myocardialSet.size() == 1);
        Assert.assertTrue(myocardialSet.iterator().next().getConfidence() > 1);

        classifications = allRecords.getRecord(72408).getClassifications();
        System.out.println(classifications);
        Assert.assertEquals(1, classifications.size());
        it = classifications.iterator();
        codesinmap = getCodesInMap(it);
        Assert.assertTrue(codesinmap.contains("I340"));

        classifications = allRecords.getRecord(6804).getClassifications();
        System.out.println(classifications);
        Assert.assertEquals(2, classifications.size());
        it = classifications.iterator();
        codesinmap = getCodesInMap(it);
        Assert.assertTrue(codesinmap.contains("I219"));
        Assert.assertTrue(codesinmap.contains("I515"));

        classifications = allRecords.getRecord(43454).getClassifications();
        System.out.println(classifications);
        Assert.assertEquals(1, classifications.size());
        it = classifications.iterator();
        codesinmap = getCodesInMap(it);
        Assert.assertTrue(codesinmap.contains("I219"));

        classifications = allRecords.getRecord(6809).getClassifications();
        System.out.println(classifications);
        Assert.assertEquals(2, classifications.size());
        it = classifications.iterator();
        codesinmap = getCodesInMap(it);
        Assert.assertTrue(codesinmap.contains("I219"));
        Assert.assertTrue(codesinmap.contains("I515"));

        classifications = allRecords.getRecord(9999).getClassifications();
        System.out.println(classifications);
        Assert.assertEquals(0, classifications.size());
        it = classifications.iterator();
        codesinmap = getCodesInMap(it);
        Assert.assertTrue(codesinmap.isEmpty());

        classifications = allRecords.getRecord(1234).getClassifications();
        System.out.println(classifications);
        Assert.assertEquals(3, classifications.size());
        it = classifications.iterator();
        codesinmap = getCodesInMap(it);
        Assert.assertTrue(codesinmap.contains("I501"));
        Assert.assertTrue(codesinmap.contains("I340"));
        Assert.assertTrue(codesinmap.contains("I38"));
        final Set<Classification> failureSet = allRecords.getRecord(1234).getListOfClassifications().get("failure of the right ventricular");
        Assert.assertEquals(1, failureSet.size());
        Assert.assertTrue(failureSet.iterator().next().getConfidence() > 1);

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
