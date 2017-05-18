/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module storr.
 *
 * storr is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * storr is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with storr. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.storr.rest;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.jersey.server.ResourceConfig;
import uk.ac.standrews.cs.storr.interfaces.IStore;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTConfig {

    private final static String STORR_API_VERSION = "1.0";
    private final static String REST_PACKAGE = "uk.ac.standrews.cs.storr.rest.api";

    public static IStore store;

    public RESTConfig(IStore store) {
        RESTConfig.store = store;

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion(STORR_API_VERSION);
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setBasePath("/");
        beanConfig.setResourcePackage(REST_PACKAGE);
        beanConfig.setTitle("storr API");
        beanConfig.setDescription("This REST API exposes the basic functionalities of storr. " +
                "storr is a NoSQL store intended to provide easy storage of arbitrary tuples");
        beanConfig.setScan(true);
        beanConfig.setPrettyPrint(true);
        beanConfig.setLicense(
                "/*\n" +
                        " * Copyright 2017 Systems Research Group, University of St Andrews:\n" +
                        " * <https://github.com/stacs-srg>\n" +
                        " *\n" +
                        " * This file is part of the module storr.\n" +
                        " *\n" +
                        " * storr is free software: you can redistribute it and/or modify it under the terms of the GNU General Public\n" +
                        " * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later\n" +
                        " * version.\n" +
                        " *\n" +
                        " * storr is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied\n" +
                        " * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.\n" +
                        " *\n" +
                        " * You should have received a copy of the GNU General Public License along with storr. If not, see\n" +
                        " * <http://www.gnu.org/licenses/>.\n" +
                        " */"
        );
    }

    public ResourceConfig build() {

        return new ResourceConfig()
                .packages(REST_PACKAGE)
                .register(ApiListingResource.class)
                .register(SwaggerSerializers.class);
    }
}
