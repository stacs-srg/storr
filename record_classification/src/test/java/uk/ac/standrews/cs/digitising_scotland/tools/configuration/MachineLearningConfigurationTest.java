/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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

        properties.setProperty("olrRegularisation", "false");
        Assert.assertEquals("false", properties.getProperty("olrRegularisation"));

    }

}
