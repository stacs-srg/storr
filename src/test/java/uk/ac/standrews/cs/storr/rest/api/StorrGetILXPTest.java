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

import org.junit.Test;
import uk.ac.standrews.cs.storr.rest.http.HTTPStatus;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StorrGetILXPTest extends RESTCommonTest {

    @Test
    public void createAndGetILXP_0() {

        String data = "{}";

        String handle = createILXPFromJSONAndTest(data);
        getILXPAndTest(handle, data);
    }

    @Test
    public void createAndGetILXP_1() {

        String data = "{ \"array\" : []}";

        String handle = createILXPFromJSONAndTest(data);
        getILXPAndTest(handle, data);
    }

    @Test
    public void createAndGetILXP_2() {

        String data = "{" +
                "\"number\" : 1" +
                "}";

        String handle = createILXPFromJSONAndTest(data);
        getILXPAndTest(handle, data);
    }

    @Test
    public void createAndGetILXP_3() {

        String data = "{" +
                "\"numbers\": [1, 2, 3]" +
                "}";

        String handle = createILXPFromJSONAndTest(data);
        getILXPAndTest(handle, data);
    }

    @Test
    public void getUknownILXP() {

        String handle = "REPO/BUCKET/000000";

        Response response = target(handle).request().get();
        assertEquals(HTTPStatus.NOT_FOUND, response.getStatus());
    }
}
