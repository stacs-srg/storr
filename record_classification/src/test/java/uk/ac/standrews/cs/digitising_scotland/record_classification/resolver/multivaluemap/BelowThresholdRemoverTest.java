package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.multivaluemap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import java.io.IOException;

/**
 * Testing BelowThresholdRemover with Codes and Classifications.
 * Created by fraserdunlop on 07/10/2014 at 11:53.
 */
public class BelowThresholdRemoverTest {

    private BelowThresholdRemover<Code,Classification,Double> belowThresholdRemover = new BelowThresholdRemover<>();
    private MultiValueMapTestHelper mvmHelper;

    @Before
    public void setup() throws IOException, CodeNotValidException {
        mvmHelper = new MultiValueMapTestHelper();
        mvmHelper.addMockEntryToMatrix("brown dog", "2100", 0.5);
        mvmHelper.addMockEntryToMatrix("white dog", "2100", 0.85);
        mvmHelper.addMockEntryToMatrix("brown dog", "2200", 0.81);
        mvmHelper.addMockEntryToMatrix("white dog", "2200", 0.87);
    }

    /**
     * Chop below confidence test.
     */
    @Test
    public void removeBelowThresholdTest() throws IOException, ClassNotFoundException {
        MultiValueMap<Code,Classification> map = mvmHelper.getMap();
        Assert.assertEquals(4, map.complexity());
        MultiValueMap<Code,Classification> matrix2 = belowThresholdRemover.removeBelowThreshold(map,0.7);
        Assert.assertEquals(2, matrix2.complexity());
    }
}
