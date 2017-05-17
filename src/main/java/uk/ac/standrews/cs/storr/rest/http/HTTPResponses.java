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
package uk.ac.standrews.cs.storr.rest.http;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class HTTPResponses {

    public static Response INTERNAL_SERVER() {
        return Response.status(HTTPStatus.INTERNAL_SERVER)
                .type(MediaType.TEXT_PLAIN)
                .entity("Something went wrong on our side. Sorry")
                .build();
    }

    public static Response BAD_REQUEST(String message) {
        return Response.status(HTTPStatus.BAD_REQUEST)
                .type(MediaType.TEXT_PLAIN)
                .entity(message)
                .build();
    }

    public static Response NOT_FOUND(String message) {
        return Response.status(HTTPStatus.NOT_FOUND)
                .type(MediaType.TEXT_PLAIN)
                .entity(message)
                .build();
    }

    public static Response CREATED(String message) {
        return Response.status(HTTPStatus.CREATED)
                .entity(message)
                .build();
    }

    public static Response OK(InputStream inputStream) {
        return Response.status(HTTPStatus.OK)
                .entity(inputStream)
                .type(MediaType.MULTIPART_FORM_DATA) // Note - this is a general media-type. will not render on browser.
                .build();
    }

    public static Response OK(String message) {
        return Response.status(HTTPStatus.OK)
                .entity(message)
                .build();
    }

}
