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

        String data = "{\n" +
                "\t\"number\" : 1\n" +
                "}";

        Response response = target("/REPO/BUCKET")
                .request()
                .post(Entity.json(data));

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
