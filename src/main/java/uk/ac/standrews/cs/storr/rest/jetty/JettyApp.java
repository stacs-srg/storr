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
package uk.ac.standrews.cs.storr.rest.jetty;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import uk.ac.standrews.cs.storr.impl.Store;
import uk.ac.standrews.cs.storr.interfaces.IStore;
import uk.ac.standrews.cs.storr.rest.config.RESTConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class JettyApp {

    private static final int DEFAULT_SERVER_PORT = 9998;
    private static UriBuilder uriBuilder = UriBuilder.fromUri("http://0.0.0.0/");
    private static int serverPort = DEFAULT_SERVER_PORT;

    private static Server StartServer(IStore store) throws Exception {

        final ResourceConfig rc = new RESTConfig().setStorr(store);
        URI baseUri = uriBuilder.port(serverPort).build();

        return JettyHttpContainerFactory.createServer(baseUri, rc);
    }

    /**
     * Start expose storr rest API via jetty server
     *
     * The following parameters are allowed:
     * - port (default port is 9998)
     * - store path
     *
     * Example:
     * port=9998 path=~/storr/
     *
     * @param args
     * @throws Exception if the server or storr could not be launched successfully
     */
    public static void main(String[] args) throws Exception {

        HashMap<String, String> map = makeMap(args);
        if (map.containsKey("port")) {
            serverPort = Integer.parseInt(map.get("port"));
        }

        Path store_path;
        if (map.containsKey("path")) {
            store_path = Paths.get(map.get("path").replaceFirst("^~",System.getProperty("user.home")));
            File path = new File(store_path.toString());
            path.mkdirs();
        } else {
            store_path = Files.createTempDirectory(null);
        }

        System.out.println("Store will be created in path " + store_path);
        System.out.println("REST API documentation available at localhost:" + serverPort +
                "/swagger.json OR localhost:" + serverPort + "/swagger.yaml");

        IStore store = new Store(store_path);

        final Server server = StartServer(store);

        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }

    }

    // Creates field_storage of key-value arguments
    private static HashMap<String, String> makeMap(String[] args) {

        HashMap<String, String> map = new HashMap<>();

        for(String arg:args) {

            if (arg.contains("=")) {
                String key = arg.substring(0, arg.indexOf("="));
                String value = arg.substring(arg.indexOf("=") + 1, arg.length());

                map.put(key, value);
            }
        }

        return map;
    }
}
