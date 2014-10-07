package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.multivaluemap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;

import java.io.IOException;

/**
 *
 * Created by fraserdunlop on 07/10/2014 at 12:22.
 */
public class FlattenerTest {

    Flattener<Code,Classification> flattener = new Flattener<>();
    private MultiValueMapTestHelper mvmHelper;

    @Before
    public void setup() throws IOException, CodeNotValidException {
        mvmHelper = new MultiValueMapTestHelper();
        mvmHelper.addMockEntryToMatrix("brown dog", "2100", 0.5);
        mvmHelper.addMockEntryToMatrix("white dog", "2100", 0.85);
        mvmHelper.addMockEntryToMatrix("brown dog", "2200", 0.81);
        mvmHelper.addMockEntryToMatrix("white dog", "2200", 0.87);
    }

    @Test
    public void testFlattener() throws IOException, ClassNotFoundException {
        MultiValueMap<Code,Classification> map = mvmHelper.getMap();
        Assert.assertEquals(4, map.complexity());
        Assert.assertEquals(2, map.size());
        MultiValueMap<Code,Classification> map2 = flattener.moveAllIntoKey(map,map.iterator().next());
        Assert.assertEquals(4, map2.complexity());
        Assert.assertEquals(1, map2.size());
    }
}
