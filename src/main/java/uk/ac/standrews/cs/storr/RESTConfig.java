package uk.ac.standrews.cs.storr;

import org.glassfish.jersey.server.ResourceConfig;
import uk.ac.standrews.cs.storr.json.JacksonProvider;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTConfig {

    private final static String REST_PACKAGE = " uk.ac.standrews.cs.storr.rest";

    public ResourceConfig build() {
        return new ResourceConfig()
                .packages(REST_PACKAGE)
                .register(JacksonProvider.class);
    }
}
