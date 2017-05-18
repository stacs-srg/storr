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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import uk.ac.standrews.cs.storr.impl.BucketKind;
import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.ILXP;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.rest.http.HTTPResponses;
import uk.ac.standrews.cs.storr.rest.http.HTTPStatus;
import uk.ac.standrews.cs.utilities.JSONReader;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.StringReader;

import static uk.ac.standrews.cs.storr.rest.config.RESTConfig.store;

/**
 * This is the actual STORR REST API.
 *
 * This class contains all the REST calls defined using the JAX-RS syntax (via Jersey).
 * The REST documentation is generated using Swagger.
 *
 * See the following link for the swagger annotations language:
 * @link https://github.com/swagger-api/swagger-core/wiki/Annotations-1.5.X
 *
 *
 * TODO - log the REST calls
 * TODO - get repo/bucket contents
 * TODO - additional operations on repo/buckets
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/")
@Api(value="/", description = "storr operations")
public class Storr {

    @GET
    @Path("/{repo}/{bucket}/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get the ILXP object matching the given id")
    @ApiResponses(value = {
            @ApiResponse(code = HTTPStatus.OK, message = "ILXP JSON structure"),
            @ApiResponse(code = HTTPStatus.INTERNAL_SERVER, message = "ILXP Not Found")
    })
    public Response getILXP(@PathParam("repo") final String repo, @PathParam("bucket") final String buck, @PathParam("id") final String id) {

        try {
            IRepository repository = store.getRepository(repo);
            IBucket bucket = repository.getBucket(buck);
            ILXP lxp = bucket.getObjectById(Long.parseLong(id));

            return HTTPResponses.OK(lxp.toString());

        } catch (BucketException | RepositoryException e) {
            return HTTPResponses.INTERNAL_SERVER();
        }

    }

    @GET
    @Path("/has/{repo}/{bucket}/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Check if the ILXP object matching the given id exists")
    @ApiResponses(value = {
            @ApiResponse(code = HTTPStatus.OK, message = "The ILXP handle"),
            @ApiResponse(code = HTTPStatus.NOT_FOUND, message = "The ILXP requested does not exists"),
            @ApiResponse(code = HTTPStatus.INTERNAL_SERVER, message = "ILXP Not Found")
    })
    public Response hasILXP(@PathParam("repo") final String repo, @PathParam("bucket") final String buck, @PathParam("id") final String id) {

        try {
            IRepository repository = store.getRepository(repo);
            IBucket bucket = repository.getBucket(buck);
            boolean exists = bucket.contains(Long.parseLong(id));

            if (exists) {
                String objectHandle = objectHandle(repo, buck, id);
                return HTTPResponses.FOUND(objectHandle);
            } else {
                return HTTPResponses.NOT_FOUND();
            }

        } catch (RepositoryException e) {
            return HTTPResponses.INTERNAL_SERVER();
        }

    }

    @POST
    @Path("/{repo}/{bucket}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Creates an ILXP object for the given JSON structure at the specified repository and bucket",
                  notes = "The ILXP object is created under the /repo/bucket/ location. If the repository and bucket do not exist, " +
                        "then storr will attempt to create them.")
    @ApiResponses(value = {
            @ApiResponse(code = HTTPStatus.CREATED, message = "The ILXP handle"),
            @ApiResponse(code = HTTPStatus.INTERNAL_SERVER, message = "The ILXP object could not be created2")
    })
    public Response postILXP(@PathParam("repo") String repo, @PathParam("bucket") String buck, String json) {

        try {
            IRepository repository = getOrMakeRepository(repo);
            IBucket bucket = getOrMakeBucket(repository, buck);

            JSONReader reader = new JSONReader(new StringReader(json));

            LXP lxp = new LXP(reader);
            bucket.makePersistent(lxp);

            String objectHandle = objectHandle(repo, buck, lxp.getId());
            return HTTPResponses.CREATED(objectHandle);

        } catch (PersistentObjectException | RepositoryException | BucketException e) {
            return HTTPResponses.INTERNAL_SERVER();
        }

    }

    @DELETE
    @Path("/{repo}/{bucket}/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Deletes the ILXP object matching the /repo/bucket/id handle")
    @ApiResponses(value = {
            @ApiResponse(code = HTTPStatus.OK, message = "The ILXP was deleted"),
            @ApiResponse(code = HTTPStatus.INTERNAL_SERVER, message = "The ILXP could not be deleted")
    })
    public Response deleteILXP(@PathParam("repo") final String repo, @PathParam("bucket") final String buck, @PathParam("id") final String id) {

        try {
            IRepository repository = store.getRepository(repo);
            IBucket bucket = repository.getBucket(buck);
            bucket.delete(Long.parseLong(id));

            String objectHandle = objectHandle(repo, buck, id);
            return HTTPResponses.OK("Object with handle " + objectHandle + " was deleted");

        } catch (BucketException | RepositoryException e) {
            return HTTPResponses.INTERNAL_SERVER();
        }
    }

    /////////////////////
    // UTILITY METHODS //
    /////////////////////

    private String objectHandle(String repo, String buck, long id) {
        return objectHandle(repo, buck, Long.toString(id));
    }

    private String objectHandle(String repo, String buck, String id) {
        return "/" + repo + "/" + buck + "/" + id;
    }

    private IRepository getOrMakeRepository(String repo) throws RepositoryException {
        IRepository repository;
        try {
            repository = store.getRepository(repo);
        } catch (RepositoryException e) {
            repository = store.makeRepository(repo);
        }

        return repository;
    }

    private IBucket getOrMakeBucket(IRepository repository, String buck) throws RepositoryException {
        IBucket bucket;
        try {
            bucket = repository.getBucket(buck);
        } catch (RepositoryException e) {
            bucket = repository.makeBucket(buck, BucketKind.DIRECTORYBACKED);
        }

        return bucket;
    }
}
