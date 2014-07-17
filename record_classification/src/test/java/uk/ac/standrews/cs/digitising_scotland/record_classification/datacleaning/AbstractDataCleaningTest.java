package uk.ac.standrews.cs.digitising_scotland.record_classification.datacleaning;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;
import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

/**
 * Testing the superclass that does all of the grunt work of file handling etc in the data cleaning steps.
 * Created by fraserdunlop on 17/07/2014 at 10:55.
 */
public class AbstractDataCleaningTest {


    private final String incorrectFilePath =  this.getClass().getResource("/AbstractDataCleaningTestIncorrectFile.txt").getPath();
    private final String correctFilePath = this.getClass().getResource("/AbstractDataCleaningCorrectFile.txt").getPath();
    private final String outputFilePath = "/AbstractDataCleaningTestOutputFile.txt";
    private static final String tokenLimit = "10000";
    private Map<String, String> correctionMap;

    @Before
    public void setup() {
        buildCorrectionMap();
    }

    private void buildCorrectionMap() {
        correctionMap = new HashMap<>();
        correctionMap.put("infeton", "infection");
        correctionMap.put("paltelet", "platelet");
        correctionMap.put("ascheemic", "ischaemic");
        correctionMap.put("vetricular", "ventricular");
        correctionMap.put("neumonia", "pneumonia");
        correctionMap.put("hart", "heart");
    }

    @Test
    public void test() throws IOException, InputFormatException {
        AbstractDataCleaner cleaner = new DummyCleaner(correctionMap);
        cleaner.runOnFile(incorrectFilePath, outputFilePath, tokenLimit);
        assertOutputFileCorrect();
    }

    @After
    public void cleanup() throws IOException {
        File file = new File(outputFilePath);
        Files.deleteIfExists(file.toPath());
    }

    private void assertOutputFileCorrect() throws IOException {
        BufferedReader brCorrect = new BufferedReader(new FileReader(new File(correctFilePath)));
        BufferedReader brOutput = new BufferedReader(new FileReader(new File(outputFilePath)));
        String line;
        while ((line = brCorrect.readLine()) != null){
            assertLineTokenSetsEqual(line,brOutput.readLine());
        }
    }

    private void assertLineTokenSetsEqual(String correctLine, String outputLine) {
        String[] correctCommaSplits = correctLine.split(Utils.getCSVComma());
        String[] outputCommaSplits = outputLine.split(Utils.getCSVComma());
        for (int i = 0 ; i < correctCommaSplits.length ; i++){
            TokenSet expected = new TokenSet(correctCommaSplits[i]);
            TokenSet actual = new TokenSet(outputCommaSplits[i]);
            assertEquals(expected, actual);
        }
    }

    class DummyCleaner extends AbstractDataCleaner {

        private final Map<String, String> correctionMap;

        public DummyCleaner(final Map<String, String> correctionMap){
            this.correctionMap = correctionMap;
        }
        @Override
        public String correct(String token) {
            System.out.println(token);
            String correction = correctionMap.get(token.toLowerCase());
            if (correction != null) {
                System.out.println("Corrected " + token + " to " + correction);
                return correction;
            }
            else
                return token;
        }
    }
}
