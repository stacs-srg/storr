package uk.ac.standrews.cs.digitising_scotland.jstore.impl.factory;


import uk.ac.standrews.cs.digitising_scotland.jstore.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.Store;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.*;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.jstore.types.LXPReferenceType;
import uk.ac.standrews.cs.digitising_scotland.jstore.types.Types;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by al on 12/09/2014.
 */
public class TypeFactory {

    private static final TypeFactory instance = new TypeFactory();
    private static final String type_repo_name = "Types_repository";
    private static final String type_Rep_bucket_name = "Type_reps";
    private static final String type_names_bucket_name = "Type_names";
    private IBucket type_reps_bucket = null;
    private IBucket type_name_bucket = null;

    private IRepository type_repo;

    private HashMap<String, IReferenceType> names_to_type_cache = new HashMap<>();
    private HashMap<Long, IReferenceType> ids_to_type_cache = new HashMap<>();

    private TypeFactory() {

        try {
            get_repo(type_repo_name);
            type_reps_bucket = get_bucket(type_Rep_bucket_name);
            type_name_bucket = get_bucket(type_names_bucket_name);
            load_caches();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    public IReferenceType createType(String json_encoded_type_descriptor_file_name, String type_name) {
        LXPReferenceType ref_type = new LXPReferenceType(json_encoded_type_descriptor_file_name);
        do_housekeeping(type_name, ref_type);
        return ref_type;
    }

    public IReferenceType createType(Class c, String type_name) {
        LXP typerep = Types.getTypeRep(c);
        LXPReferenceType ref_type = new LXPReferenceType(typerep);
        do_housekeeping(type_name, ref_type);
        return ref_type;
    }

    public static TypeFactory getInstance() {
        return instance;
    }

    public IReferenceType typeWithname(String name) {
        return names_to_type_cache.get(name);
    }

    public boolean containsKey(String name) {
        return names_to_type_cache.containsKey(name);
    }

    public IReferenceType typeWithId(long id) {
        return ids_to_type_cache.get(id);
    }

    public boolean containsId(long id) {
        return ids_to_type_cache.containsKey(id);
    }

    //****************** private methods ******************//

    private void load_caches() {

        // private HashMap<String,IReferenceType> names_to_type_cache = new HashMap<>();

        // private HashMap<Integer,IReferenceType> ids_to_type_cache = new HashMap<>();

        try {
            Iterator<LXP> i = type_name_bucket.getInputStream().iterator();
            while (i.hasNext()) {
                ILXP lxp = i.next();
                // as set up in @code namevaluepair below.
                String name = lxp.getString("name");
                long type_key = lxp.getLong("key");

                ILXP type_rep = type_reps_bucket.getObjectById(type_key);
                LXPReferenceType reference = new LXPReferenceType((LXP) (type_rep));

                names_to_type_cache.put(name, reference);
                ids_to_type_cache.put(type_key, reference);
            }
        } catch (BucketException e) {
            ErrorHandling.exceptionError(e, "IO exception getting iterator over type name map");
        } catch (KeyNotFoundException e) {
            ErrorHandling.exceptionError(e, "Could not find key whilst reestablising caches");
        } catch (TypeMismatchFoundException e) {
            ErrorHandling.exceptionError(e, "Type mismatch");
        }

    }

    private void do_housekeeping(String type_name, LXPReferenceType ref_type) {
        try {
            ILXP type_rep = ref_type.getRep();
            ILXP name_value = namevaluepair(type_name, type_rep.getId());
            type_reps_bucket.makePersistent(type_rep);
            type_name_bucket.makePersistent(name_value);
        } catch (BucketException e) {
            ErrorHandling.exceptionError(e, "Bucket exception adding type " + type_name + " to types bucket");
        }
        names_to_type_cache.put(type_name, ref_type);
        ids_to_type_cache.put(ref_type.getId(), ref_type);
    }

    private ILXP namevaluepair(String type_name, long typekey) {
        LXP lxp = new LXP();
        // used in load_caches above
        try {
            lxp.put("name", type_name);
            lxp.put("key", typekey);
        } catch (IllegalKeyException e) {
            // ignore this clearly cannot happen!
        }
        return lxp;
    }

    private void get_repo(String type_repo_name) throws RepositoryException {
        IStore store = Store.getInstance();

        if (store.repoExists(type_repo_name)) {
            type_repo = store.getRepo(type_repo_name);
        } else {
            ErrorHandling.error("Didn't find types repository creating new one called: " + type_repo_name);
            type_repo = store.makeRepository(type_repo_name);
        }
    }

    private IBucket get_bucket(String bucket_name) throws RepositoryException {
        if (type_repo.bucketExists(bucket_name)) {
            return type_repo.getBucket(bucket_name);
        } else {
            ErrorHandling.error("Didn't find types bucket, creating a new types bucket called: " + bucket_name);
            return type_repo.makeBucket(bucket_name, BucketKind.DIRECTORYBACKED);
        }
    }

}
