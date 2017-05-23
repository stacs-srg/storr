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
import org.junit.Test;
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

import static org.junit.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StorrTest extends JerseyTest {

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

    @Test
    public void createBasicILXP() {

        String data = "{" +
                "\"number\" : 1" +
                "}";

        createILXPFromJSON(data);
    }

    @Test
    public void createILXPFromJSON_1() {

        String data = "{" +
                "    \"glossary\": {" +
                "        \"title\": \"test glossary\"," +
                "        \"GlossDiv\": {" +
                "            \"title\": \"S\"," +
                "            \"valid\": true" +
                "        }" +
                "    }" +
                "}";

        createILXPFromJSON(data);
    }

    @Test
    public void createILXPFromJSON_2() {

        String data = "{" +
                "\"name\": \"John\"," +
                "\"age\": 30," +
                "\"cars\": [\"Ford\", \"BMW\", \"Fiat\", \"Ferrari\"]" +
                "}";

        createILXPFromJSON(data);
    }

    @Test
    public void createILXPFromJSON_3() {

        String data = "{" +
                "   \"glossary\": [{" +
                "    \"title\": \"test title\"" +
                "}]" +
                "}";

        createILXPFromJSON(data);
    }

    @Test
    public void createILXPFromJSON_4() {

        String data = "{" +
                "  \"problems\": [{" +
                "    \"Diabetes\": [{" +
                "      \"medications\": [{" +
                "        \"medicationsClasses\": [{" +
                "          \"className\": [{" +
                "            \"associatedDrug\": [{" +
                "              \"name\": \"asprin\"," +
                "              \"dose\": \"\"," +
                "              \"strength\": \"500 mg\"" +
                "            }]," +
                "            \"associatedDrug#2\": [{" +
                "              \"name\": \"somethingElse\"," +
                "              \"dose\": \"\"," +
                "              \"strength\": \"500 mg\"" +
                "            }]" +
                "          }]," +
                "          \"className2\": [{" +
                "            \"associatedDrug\": [{" +
                "              \"name\": \"asprin\"," +
                "              \"dose\": \"\"," +
                "              \"strength\": \"500 mg\"" +
                "            }]," +
                "            \"associatedDrug#2\": [{" +
                "              \"name\": \"somethingElse\"," +
                "              \"dose\": \"\"," +
                "              \"strength\": \"500 mg\"" +
                "            }]" +
                "          }]" +
                "        }]" +
                "      }]," +
                "      \"labs\": [{" +
                "        \"missing_field\": \"missing_value\"" +
                "      }]" +
                "    }]" +
                "  }]" +
                "}";

        createILXPFromJSON(data);
    }

    private void createILXPFromJSON(String JSON) {
        Response response = target("/REPO/BUCKET")
                .request()
                .post(Entity.json(JSON));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);

        // Handle should be: /REPO/BUCKET/long_number
        String[] ILXPHandle = response.readEntity(String.class).split("/");
        assertEquals(ILXPHandle.length, 4);
        assertTrue(ILXPHandle[0].isEmpty());
        assertEquals(ILXPHandle[1], "REPO");
        assertEquals(ILXPHandle[2], "BUCKET");
        assertFalse(ILXPHandle[3].isEmpty());

    }
}
