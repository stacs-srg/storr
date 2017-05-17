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

import uk.ac.standrews.cs.storr.impl.BucketKind;
import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.ILXP;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.rest.http.HTTPResponses;
import uk.ac.standrews.cs.utilities.JSONReader;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.StringReader;

import static uk.ac.standrews.cs.storr.rest.RESTConfig.store;

/**
 * TODO - log the REST calls
 * TODO - get repo/bucket
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/")
public class Storr {

    @GET
    @Path("/{repo}/{bucket}/{id}")
    @Produces(MediaType.APPLICATION_JSON)
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

    @POST
    @Path("/{repo}/{bucket}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postILXP(@PathParam("repo") String repo, @PathParam("bucket") String buck, String json) {

        try {
            IRepository repository = getOrMakeRepository(repo);
            IBucket bucket = getOrMakeBucket(repository, buck);

            JSONReader reader = new JSONReader(new StringReader(json));

            LXP lxp = new LXP(reader);
            bucket.makePersistent(lxp);

            String objectHandle = repo + "/" + buck + "/" + lxp.getId();
            return HTTPResponses.CREATED(objectHandle);

        } catch (PersistentObjectException | RepositoryException | BucketException e) {
            return HTTPResponses.INTERNAL_SERVER();
        }

    }

    @DELETE
    @Path("/{repo}/{bucket}/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteILXP(@PathParam("repo") final String repo, @PathParam("bucket") final String buck, @PathParam("id") final String id) {

        try {
            IRepository repository = store.getRepository(repo);
            IBucket bucket = repository.getBucket(buck);
            bucket.delete(Long.parseLong(id));

            String objectHandle = repo + "/" + buck + "/" + id;
            return HTTPResponses.OK("Object with handle " + objectHandle + " was deleted");

        } catch (BucketException | RepositoryException e) {
            return HTTPResponses.INTERNAL_SERVER();
        }
    }

    // UTILITY METHODS

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
