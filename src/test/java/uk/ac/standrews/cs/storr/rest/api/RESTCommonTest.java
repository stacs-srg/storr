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
package uk.ac.standrews.cs.storr.rest.api;

import org.glassfish.jersey.test.JerseyTest;
import org.skyscreamer.jsonassert.JSONAssert;
import uk.ac.standrews.cs.storr.impl.Store;
import uk.ac.standrews.cs.storr.interfaces.IStore;
import uk.ac.standrews.cs.storr.rest.config.RESTConfig;
import uk.ac.standrews.cs.storr.rest.http.HTTPStatus;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTCommonTest extends JerseyTest {

    @Override
    protected Application configure() {

        try {
            Path store_path = Files.createTempDirectory(null);
            IStore store = new Store(store_path);

            return new RESTConfig().setStorr(store);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected String createILXPFromJSONAndTest(String JSON) {
        Response response = target("/REPO/BUCKET")
                .request()
                .post(Entity.json(JSON));

        assertEquals(HTTPStatus.CREATED, response.getStatus());

        // Handle should be: REPO/BUCKET/long_number
        String[] ILXPHandle = response.readEntity(String.class).split("/");
        assertEquals(3, ILXPHandle.length);
        assertEquals("REPO", ILXPHandle[0]);
        assertEquals("BUCKET", ILXPHandle[1]);
        assertFalse(ILXPHandle[2].isEmpty());

        return String.join("/", ILXPHandle);
    }

    protected void createILXPFromJSONAndFailTest(String JSON) {
        Response response = target("/REPO/BUCKET")
                .request()
                .post(Entity.json(JSON));

        assertEquals(response.getStatus(), HTTPStatus.INTERNAL_SERVER);
    }

    protected void getILXPAndTest(String handle, String matchingJSON) {

        Response response = target(handle).request().get();
        assertEquals(HTTPStatus.OK, response.getStatus());

        String json = response.readEntity(String.class);
        JSONAssert.assertEquals(matchingJSON, json, true);
    }
}