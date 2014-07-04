package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IRepository;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IStore;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IStoreIndex;

import java.util.Iterator;

/**
 * A simple store index that relies on dumb searching of the directories in the store
 * Would be possible to write a much more sophisticated index - perhaps using hard links etc.
 * YAGNI
 *
 * Created by al on 04/07/2014.
 */
public class StoreIndex implements IStoreIndex {

    private IStore store;

    public StoreIndex(Store store) {
        this.store = store;
    }

    @Override
    public IBucket get(int id) {

        Iterator<IRepository> repo_iterator = store.getIterator();
        while (repo_iterator.hasNext()) {

            IRepository repo = repo_iterator.next();

            Iterator<IBucket> bucket_iterator = repo.getIterator();

            while (bucket_iterator.hasNext()) {

                IBucket bucket = bucket_iterator.next();

                if( bucket.contains(id) ) {

                    return bucket;
                }

            }
        }
        // we didn't find it
        return null;
    }
}
