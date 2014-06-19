package uk.ac.standrews.cs.digitising_scotland.tools.configuration;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This Class, MachineLearningConfigurationTest, aims to test the creation and use of properties files
 * that are used to confiure the behaviour of the learners.
 */
public class MachineLearningConfigurationTest {

    private Properties properties = MachineLearningConfiguration.getDefaultProperties();

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {

    }

    /**
     * Test no default overwrite.
     */
    @Test
    public void testNoDefaultOverwrite() {

        Assert.assertEquals("0.0009", properties.getProperty("finalRate"));
        System.out.println(properties.entrySet());
    }

    /**
     * Test default overwritten.
     */
    @Test
    public void testDefaultOverwritten() {

        System.out.println(properties.getProperty("reps"));
        //Assert.assertEquals("13", properties.getProperty("reps"));

    }

    /**
     * Test get property with changed from default.
     */
    @Test
    public void testGetPropertyWithChangedFromDefault() {

        Assert.assertEquals("false", properties.getProperty("olrRegularisation"));

    }

}
