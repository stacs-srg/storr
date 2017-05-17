package uk.ac.standrews.cs.storr.rest.api;

import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.interfaces.ILXP;
import uk.ac.standrews.cs.storr.rest.http.HTTPResponses;
import uk.ac.standrews.cs.utilities.JSONReader;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.StringReader;

import static uk.ac.standrews.cs.storr.rest.RESTConfig.bucket;
import static uk.ac.standrews.cs.storr.rest.RESTConfig.repo;

/**
 * TODO - log the REST calls
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/")
public class Storr {

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJSON(@PathParam("id") String jsonId) {

        try {
            ILXP lxp = bucket.getObjectById(Long.parseLong(jsonId));

            return HTTPResponses.OK(lxp.toString());
        } catch (BucketException e) {
            return HTTPResponses.INTERNAL_SERVER();
        }

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postJSON(String json) {

        try {
            JSONReader reader = new JSONReader(new StringReader(json));

            LXP lxp = new LXP(reader, repo, bucket);
            bucket.makePersistent(lxp);

            return HTTPResponses.CREATED(String.valueOf(lxp.getId()));

        } catch (PersistentObjectException | BucketException e) {
            return HTTPResponses.INTERNAL_SERVER();
        }

    }
}
