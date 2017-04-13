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
package uk.ac.standrews.cs.storr.jetty;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import uk.ac.standrews.cs.storr.rest.RESTConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class JettyApp {

    private static final int DEFAULT_SERVER_PORT = 9998;
    public static UriBuilder uriBuilder = UriBuilder.fromUri("http://0.0.0.0/");
    private static int serverPort;
    private static URI baseUri;

    public static Server startServer() throws Exception {
        final ResourceConfig rc = new RESTConfig().build();

        baseUri = uriBuilder.port(serverPort).build();
        return JettyHttpContainerFactory.createServer(baseUri, rc);
    }

    /**
     * Start expose storr rest API via jetty server
     *
     * The following parameters are allowed:
     * - port (default port is 9998)
     * Example:
     *
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            serverPort = Integer.parseInt(args[0]);
        } else {
            serverPort = DEFAULT_SERVER_PORT;
        }

        final Server server = startServer();

        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }

    }
}
