package uk.ac.standrews.cs.storr.utils;

import uk.ac.standrews.cs.storr.InfrastructureTest;

import java.io.File;
import java.net.URISyntaxException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Helper {

    /**
     * Returns the string path representation of a given resource under /test/resources/
     *
     * @param resource
     * @return
     * @throws URISyntaxException
     */
    public static String getResourcePath(String resource) throws URISyntaxException {
        return new File(InfrastructureTest.class.getResource("/" + resource).toURI()).getAbsolutePath();
    }
}
