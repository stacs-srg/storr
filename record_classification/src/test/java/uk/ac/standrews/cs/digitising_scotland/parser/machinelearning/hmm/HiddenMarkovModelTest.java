package uk.ac.standrews.cs.digitising_scotland.parser.machinelearning.hmm;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.parser.machinelearning.hmm.HiddenMarkovModel;
import cc.mallet.types.Alphabet;

public class HiddenMarkovModelTest {

    @Test
    public void test() throws IOException {

        String trainingFilename = getClass().getResource("/hmmTrainingTest.txt").getFile();
        String testingFilename = getClass().getResource("/occupationTestFormatPipe.txt").getFile();
        HiddenMarkovModel hmm = new HiddenMarkovModel(trainingFilename, testingFilename);
        Alphabet alphabet = hmm.generateAlphabet(trainingFilename);
        Object[] arr = convertAplhabetToSortedArray(alphabet);

        String[] expected = {"", "; SP", "hemiplegia C1", "phthisis C2", "lung C1", "of C1", "congestion C2", "inanition O", "pseudo-hypertrophic O", "brain C1", "tubercle C1", "diarrhoea C1", "incipient SP", "both SP", "of O", "paralysis C1", "haemoptysis C2"};
        Arrays.sort(expected);

        Assert.assertArrayEquals(expected, arr);
    }

    private Object[] convertAplhabetToSortedArray(final Alphabet alphabet) {

        Object[] arr = alphabet.toArray();
        Arrays.asList(arr).toArray(new String[arr.length]);
        Arrays.sort(arr);
        return arr;
    }

    @Test
    public void testTrain() throws IOException {

        String trainingFilename = getClass().getResource("/hmmTrainingTest.txt").getFile();
        String testingFilename = getClass().getResource("/hmmOutputTest.txt").getFile();
        HiddenMarkovModel hmm = new HiddenMarkovModel(trainingFilename, testingFilename);
        hmm.trainHmm("target/hmmOutput.txt");
    }
}
