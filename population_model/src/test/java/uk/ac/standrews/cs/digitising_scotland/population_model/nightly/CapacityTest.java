package uk.ac.standrews.cs.digitising_scotland.population_model.nightly;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPopulation;
import uk.ac.standrews.cs.test.category.Slow;

/**
 * Tests to see if we can cope with capacity.
 * 
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
@Category(Slow.class)
// Test run only nightly.
public class CapacityTest {

    private static final int POPULATION_SIZE = 1000000;

    @Ignore
    @Test
    public void capacity() throws Exception {

        /*
         * Java VM arguments:
         * 
         * $ java -d64 -Xms512m -Xmx4g HelloWorld where,
         * 
         * -d64: Will enable 64-bit JVM -Xms512m: Will set initial heap size as 512 MB -Xmx4g: Will set maximum heap size as 4 GB
         */

        new CompactPopulation(POPULATION_SIZE, CompactPopulation.START_YEAR, CompactPopulation.END_YEAR, null);
    }
}
