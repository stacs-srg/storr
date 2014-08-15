package uk.ac.standrews.cs.digitising_scotland.record_classification.datacleaning;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

/**
 * Testing the superclass that does all of the grunt work of file handling etc in the data cleaning steps.
 * Created by fraserdunlop on 17/07/2014 at 10:55.
 */
public class AbstractDataCleanerTest {

    private final String incorrectFilePath = this.getClass().getResource("/AbstractDataCleaningTestIncorrectFile.txt").getPath();
    private final String correctFilePath = this.getClass().getResource("/AbstractDataCleaningCorrectFile.txt").getPath();
    private static final String outputFilePath = "AbstractDataCleaningTestOutputFile.txt";
    private static final String TOKENLIMIT = "10000";
    private Map<String, String> correctionMap;

    @Before
    public void setup() {

        CodeFactory.getInstance().loadDictionary(new File(getClass().getResource("/CodeFactoryCoDFile.txt").getFile()));
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
        cleaner.runOnFile(incorrectFilePath, outputFilePath, TOKENLIMIT);
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
        while ((line = brCorrect.readLine()) != null) {
            assertLineTokenSetsEqual(line, brOutput.readLine());
        }
        brCorrect.close();
        brOutput.close();
    }

    private void assertLineTokenSetsEqual(final String correctLine, final String outputLine) {

        String[] correctCommaSplits = correctLine.split(Utils.getCSVComma());
        String[] outputCommaSplits = outputLine.split(Utils.getCSVComma());
        for (int i = 0; i < correctCommaSplits.length; i++) {
            TokenSet expected = new TokenSet(correctCommaSplits[i]);
            TokenSet actual = new TokenSet(outputCommaSplits[i]);
            assertEquals(expected, actual);
        }
    }

    class DummyCleaner extends AbstractDataCleaner {

        private final Map<String, String> correctionMap;

        public DummyCleaner(final Map<String, String> correctionMap) {

            this.correctionMap = correctionMap;
        }

        @Override
        public String correct(final String token) {

            System.out.println(token);
            String correction = correctionMap.get(token.toLowerCase());
            if (correction != null) {
                System.out.println("Corrected " + token + " to " + correction);
                return correction;
            }
            else {
                return token;
            }
        }
    }
}
