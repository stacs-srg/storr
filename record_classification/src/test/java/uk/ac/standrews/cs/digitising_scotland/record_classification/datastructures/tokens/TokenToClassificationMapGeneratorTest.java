package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.OriginalData;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.runners.Parameterized.Parameters;


/**
 *
 * Created by fraserdunlop on 09/10/2014 at 14:37.
 */
@RunWith(Parameterized.class)
public class TokenToClassificationMapGeneratorTest {
    CodeDictionary codeDictionary;
    TokenToClassificationMapGenerator mapGenerator = new TokenToClassificationMapGenerator();
    String[] descriptions;

    public TokenToClassificationMapGeneratorTest(final String[] descriptions){
        this.descriptions = descriptions;
    }

    @Parameters
    public static Collection<String[][]> strings() {
        return Arrays.asList(new String[][][]{
                {{"boom", "bang", "shadows taller than their souls","boom"}},
                {{"shake", "sheek", "thousands", "hundreds", "hunners", "many many hunners of thoosands", "shake"}},
                       {{"so long and", "thanks for all the fish"}},
                               {{"Einstein", "Schrodinger","bye bye forbifarten"}},
                                       {{"cat in a ", "box","hej lezen een krant", "een krant", "een jongen", "een jongen"}},
                                              {{"off she ", "hops"}},
                                                       {{"she", "jumps from the box","off the box","nox","box","box"}}
        });
    }

    @Before
    public void setup() throws IOException {
        String codeDictionaryPath = getClass().getResource("/modCodeDictionary.txt").getFile();
        File codeDictionaryFile = new File(codeDictionaryPath);
        codeDictionary = new CodeDictionary(codeDictionaryFile);
    }

    @Test
    public void getClassificationsTest() throws InputFormatException, CodeNotValidException {
        Record record = buildRecord(descriptions);
        List<Classification> classifications = mapGenerator.getClassifications(record);
        int size = getNumUniqueTokenSets(descriptions);
        Assert.assertEquals(size, classifications.size());
    }

    private Record buildRecord(final String[] descriptions) throws InputFormatException, CodeNotValidException {
        List<String> description = new ArrayList<>();
        OriginalData originalData = new OriginalData(description,0,1,"file");
        Set<Classification> classifications = buildClassifications(descriptions);
        originalData.setGoldStandardClassification(classifications);
        return new Record(1,originalData);
    }

    private Set<Classification> buildClassifications(final String[] descriptions) throws CodeNotValidException {
        Set<Classification> set = new HashSet<>();
        double uniquifier = 0.01;
        double confidence = 0.01;
        for (String description : descriptions) {
            confidence += uniquifier;
            set.add(new Classification(codeDictionary.getCode("J98"), new TokenSet(description), confidence));
        }
        return set;
    }

    public int getNumUniqueTokenSets(String[] descriptions) {
        Map<TokenSet,Integer> map = new HashMap<>();
        for(String description : descriptions){
            TokenSet key = new TokenSet(description);
            if (map.containsKey(key)) {
                int count = map.get(key);
                count++;
                map.put(key,count);
            } else {
                map.put(key,1);
            }
        }
        int numUniqueTokenSets = 0;
        for (TokenSet key : map.keySet()){
            if (map.get(key)==1){
                numUniqueTokenSets++;
            }
        }
        return numUniqueTokenSets;
    }
}
