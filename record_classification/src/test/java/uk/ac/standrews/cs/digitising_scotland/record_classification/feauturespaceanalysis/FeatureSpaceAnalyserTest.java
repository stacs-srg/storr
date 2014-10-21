package uk.ac.standrews.cs.digitising_scotland.record_classification.feauturespaceanalysis;

import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.featurespaceanalysis.FeatureSpaceAnalyser;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.BucketGenerator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 *
 * Created by fraserdunlop on 13/10/2014 at 15:52.
 */
public class FeatureSpaceAnalyserTest {

//    @Test
//    public void test() throws IOException, CodeNotValidException, InputFormatException {
//        File trainingFile = new File(getClass().getResource("/TrainingDataModernCODFormatTest.txt").getFile());
//        Bucket bucket = createTrainingBucket(trainingFile);
//        FeatureSpaceAnalyser featureSpaceAnalyser = new FeatureSpaceAnalyser(bucket);
//        File codeFile = new File(getClass().getResource("/modCodeDictionary.txt").getFile());
//        CodeDictionary codeDictionary = new CodeDictionary(codeFile);
//        HashMap<String, Integer> map = featureSpaceAnalyser.featureProfile(codeDictionary.getCode("R54"));
//        System.out.println(map);
//    }
//
//    private Bucket createTrainingBucket(final File trainingFile) throws IOException, InputFormatException, CodeNotValidException {
//
//        String codeDictionaryPath = getClass().getResource("/modCodeDictionary.txt").getFile();
//        File codeDictionaryFile = new File(codeDictionaryPath);
//        CodeDictionary codeDictionary = new CodeDictionary(codeDictionaryFile);
//        BucketGenerator b = new BucketGenerator(codeDictionary);
//        return b.generateTrainingBucket(trainingFile);
//    }
//

}
