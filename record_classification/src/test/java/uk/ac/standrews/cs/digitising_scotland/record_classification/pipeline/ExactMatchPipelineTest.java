package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.OriginalData;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;

import com.google.common.collect.HashMultimap;

public class ExactMatchPipelineTest {

    ExactMatchClassifier exactMatchClassifier;
    ExactMatchPipeline pipeline;
    CodeDictionary codeDictionary;

    @Before
    public void setUp() throws Exception {

        File trainingFile = new File(getClass().getResource("/TrainingDataModernCODFormatTest.txt").getFile());
        Bucket trainingBucket = createTrainingBucket(trainingFile);
        exactMatchClassifier = new ExactMatchClassifier(trainingBucket);
        pipeline = new ExactMatchPipeline(exactMatchClassifier);
    }

    private Bucket createTrainingBucket(final File trainingFile) throws IOException, InputFormatException, CodeNotValidException {

        String codeDictionaryPath = getClass().getResource("/modCodeDictionary.txt").getFile();
        File codeDictionaryFile = new File(codeDictionaryPath);
        codeDictionary = new CodeDictionary(codeDictionaryFile);
        BucketGenerator b = new BucketGenerator(codeDictionary);
        return b.generateTrainingBucket(trainingFile);
    }

    @Test
    public void testFirstDescriptionLookup() throws IOException, CodeNotValidException {

        Assert.assertEquals(codeDictionary.getCode("I26"), exactMatchClassifier.classify(new TokenSet("Massive Pulmonary Embolism")).getLeft());
    }

    @Test
    public void testSecondDescriptionLookup() throws IOException, CodeNotValidException {

        Assert.assertEquals(codeDictionary.getCode("R54"), exactMatchClassifier.classify(new TokenSet("old age")).getLeft());
    }

    @Test
    public void testNoLookupLookup() throws IOException, CodeNotValidException {

        Assert.assertEquals(null, exactMatchClassifier.classify(new TokenSet("foobar")));
    }

    @Test
    public void testPipelineFirstDescriptionLookup() throws IOException, CodeNotValidException {

        Assert.assertTrue(pipeline.classify("Massive Pulmonary Embolism").iterator().next().getCode().equals(codeDictionary.getCode("I26")));
    }

    @Test
    public void testPipelineSecondDescriptionLookup() throws IOException, CodeNotValidException {

        Assert.assertTrue(pipeline.classify("old age").iterator().next().getCode().equals(codeDictionary.getCode("R54")));
    }

    @Test
    public void testPipelineNoLookupLookup() throws IOException, CodeNotValidException {

        Assert.assertEquals(null, pipeline.classify("foobar"));
    }

    @Test
    public void addResultToRecordTest() throws InputFormatException, IOException, CodeNotValidException {

        Record record = buildRecord(0, "description");

        String description = "new description";
        Set<Classification> result = pipeline.classify("old age");
        pipeline.addResultToRecord(record, description, result);
        final HashMultimap<String, Classification> listOfClassifications = record.getListOfClassifications();
        Assert.assertTrue(listOfClassifications.containsKey(description));
        Assert.assertTrue(listOfClassifications.get(description).iterator().next().getCode().equals(codeDictionary.getCode("R54")));

    }

    @Test
    public void addResultsToRecordTest() throws InputFormatException, IOException, CodeNotValidException {

        Record record = buildRecord(0, "decription");

        String description = "new description";
        Set<Classification> result1 = pipeline.classify("old age");
        Set<Classification> result2 = pipeline.classify("Massive Pulmonary Embolism");
        result1.add(result2.iterator().next());

        pipeline.addResultToRecord(record, description, result1);
        final HashMultimap<String, Classification> listOfClassifications = record.getListOfClassifications();
        Assert.assertTrue(listOfClassifications.containsKey(description));
        final Iterator<Classification> iterator = listOfClassifications.get(description).iterator();
        Assert.assertTrue(iterator.next().getCode().equals(codeDictionary.getCode("R54")));
        Assert.assertTrue(iterator.next().getCode().equals(codeDictionary.getCode("I26")));

    }

    @Test
    public void testClassifyBucket() throws InputFormatException, IOException, CodeNotValidException {

        List<Record> list = new ArrayList<>();
        list.add(buildRecord(0, "old age"));
        list.add(buildRecord(1, "Massive Pulmonary Embolism"));
        list.add(buildRecord(2, "foo"));
        Bucket bucket = new Bucket(list);
        Bucket classified = pipeline.classify(bucket, true);

        Assert.assertEquals(codeDictionary.getCode("R54"), classified.getRecord(0).getListOfClassifications().get("old age").iterator().next().getCode());
        Assert.assertEquals(codeDictionary.getCode("I26"), classified.getRecord(1).getListOfClassifications().get("Massive Pulmonary Embolism").iterator().next().getCode());
        Assert.assertNull(classified.getRecord(2));

    }

    private Record buildRecord(int i, String description) throws InputFormatException {

        List<String> descriptionList = new ArrayList<>();
        descriptionList.add(description);
        OriginalData originalData = new OriginalData(descriptionList, 2014, 1, "filename");
        Record record = new Record(i, originalData);
        return record;
    }
}
