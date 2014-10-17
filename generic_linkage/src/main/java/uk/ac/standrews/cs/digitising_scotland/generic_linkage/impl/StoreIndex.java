package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.*;

import java.util.Iterator;

/**
 * A simple store index that relies on dumb searching of the directories in the store
 * Would be possible to write a much more sophisticated index - perhaps using hard links etc.
 * YAGNI
 *
 * Created by al on 04/07/2014.
 */
public class StoreIndex<T extends ILXP> implements IStoreIndex<T> {

    private IStore store;

    public StoreIndex(Store store) {
        this.store = store;
    }

    @Override
    public IBucket get(int id) {

        Iterator<IRepository> repo_iterator = store.getIterator();
        while (repo_iterator.hasNext()) {

            IRepository repo = repo_iterator.next();

            Iterator<String> bucket_iterator = repo.getBucketNameIterator();

            while (bucket_iterator.hasNext()) {

                IBucket bucket = null;
                try {
                    bucket = repo.getBucket(bucket_iterator.next());
                    if( ! bucket.getKind().equals(BucketKind.INDIRECT ) && bucket.contains(id) ) {  // only look at primary storage

                        return bucket;
                    }
                } catch (RepositoryException e) {
                    e.printStackTrace();
                }
            }
        }
        // we didn't find it
        return null;
    }
}
