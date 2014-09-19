package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeIndexer;

@Ignore("dont try and run this test just now, needs appropraite datasets")
public class ClassifyWithExsistingModelsTest {

    @Test
    public void test() throws Exception {

        CodeIndexer.getInstance().loadDictionary(new File("icdCodes.txt"));
        String[] args0 = {"icd500.csv", "/Users/jkc25/workspace/digitising_scotland/record_classification/Experiments/Experiment206/Models"};
        ClassifyWithExsistingModels.main(args0);

    }
}
