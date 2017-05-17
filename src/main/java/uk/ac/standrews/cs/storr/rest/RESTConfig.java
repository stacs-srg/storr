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

import org.glassfish.jersey.server.ResourceConfig;
import uk.ac.standrews.cs.storr.impl.BucketKind;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.interfaces.IStore;
import uk.ac.standrews.cs.storr.rest.json.JacksonProvider;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTConfig {

    private final static String REST_PACKAGE = " uk.ac.standrews.cs.storr.rest.api";

    public static IStore store;
    public static IRepository repo;
    public static IBucket bucket;

    public RESTConfig(IStore store) {
        RESTConfig.store = store;

        // FIXME - this is temporary (see linkage-java for examples)
        try {
            repo = store.makeRepository("test");
            bucket = repo.makeBucket("bucket", BucketKind.DIRECTORYBACKED);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

    }

    public ResourceConfig build() {
        return new ResourceConfig()
                .packages(REST_PACKAGE)
                .register(JacksonProvider.class);
    }
}
